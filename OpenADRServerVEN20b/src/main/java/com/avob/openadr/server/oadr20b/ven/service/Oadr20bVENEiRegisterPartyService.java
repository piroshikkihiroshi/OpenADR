package com.avob.openadr.server.oadr20b.ven.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.eclipse.jetty.http.HttpStatus;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.avob.openadr.client.xmpp.oadr20b.ven.OadrXmppVenClient20b;
import com.avob.openadr.model.oadr20b.Oadr20bJAXBContext;
import com.avob.openadr.model.oadr20b.builders.Oadr20bEiRegisterPartyBuilders;
import com.avob.openadr.model.oadr20b.builders.Oadr20bResponseBuilders;
import com.avob.openadr.model.oadr20b.builders.eiregisterparty.Oadr20bCreatePartyRegistrationBuilder;
import com.avob.openadr.model.oadr20b.ei.SchemaVersionEnumeratedType;
import com.avob.openadr.model.oadr20b.errorcodes.Oadr20bApplicationLayerErrorCode;
import com.avob.openadr.model.oadr20b.exception.Oadr20bApplicationLayerException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bHttpLayerException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bMarshalException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bUnmarshalException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bXMLSignatureException;
import com.avob.openadr.model.oadr20b.exception.Oadr20bXMLSignatureValidationException;
import com.avob.openadr.model.oadr20b.oadr.OadrCancelPartyRegistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrCanceledPartyRegistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrCreatePartyRegistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrCreatedPartyRegistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrPayload;
import com.avob.openadr.model.oadr20b.oadr.OadrQueryRegistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrRequestReregistrationType;
import com.avob.openadr.model.oadr20b.oadr.OadrResponseType;
import com.avob.openadr.model.oadr20b.oadr.OadrTransportType;
import com.avob.openadr.security.exception.OadrSecurityException;
import com.avob.openadr.server.oadr20b.ven.MultiVtnConfig;
import com.avob.openadr.server.oadr20b.ven.VenConfig;
import com.avob.openadr.server.oadr20b.ven.VtnSessionConfiguration;

@Service
public class Oadr20bVENEiRegisterPartyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Oadr20bVENEiRegisterPartyService.class);

	@Resource
	private XmlSignatureService xmlSignatureService;

	@Resource
	private MultiVtnConfig multiVtnConfig;

	@Resource
	private Oadr20bJAXBContext jaxbContext;

	@Resource
	private VenConfig venConfig;

	private Map<String, OadrCreatedPartyRegistrationType> registration = new ConcurrentHashMap<String, OadrCreatedPartyRegistrationType>();

	private List<Oadr20bVENEiRegisterPartyServiceListener> listeners;

	public interface Oadr20bVENEiRegisterPartyServiceListener {
		public void onRegistrationSuccess(VtnSessionConfiguration vtnConfiguration,
				OadrCreatedPartyRegistrationType registration);

		public void onRegistrationError(VtnSessionConfiguration vtnConfiguration);
	}

	public OadrResponseType oadrRequestReregistration(VtnSessionConfiguration vtnConfiguration,
			OadrRequestReregistrationType oadrRequestReregistrationType) {

		String requestId = "";
		String venID = oadrRequestReregistrationType.getVenID();
		int responseCode = HttpStatus.OK_200;
		reinitRegistration(vtnConfiguration);

		return Oadr20bResponseBuilders.newOadr20bResponseBuilder(requestId, responseCode, venID).build();
	}

	public OadrCanceledPartyRegistrationType oadrCancelPartyRegistration(VtnSessionConfiguration vtnConfiguration,
			OadrCancelPartyRegistrationType oadrCancelPartyRegistrationType) {

		String venID = oadrCancelPartyRegistrationType.getVenID();

		String requestID = oadrCancelPartyRegistrationType.getRequestID();
		String registrationID = oadrCancelPartyRegistrationType.getRegistrationID();
		int responseCode = HttpStatus.OK_200;
		if (getRegistration(vtnConfiguration).getRegistrationID().equals(registrationID)) {
			clearRegistration(vtnConfiguration);
		} else {
			responseCode = Oadr20bApplicationLayerErrorCode.INVALID_ID_452;
		}

		return Oadr20bEiRegisterPartyBuilders
				.newOadr20bCanceledPartyRegistrationBuilder(requestID, responseCode, registrationID, venID).build();

	}

	public void clearRegistration(VtnSessionConfiguration vtnConfiguration) {
		setRegistration(vtnConfiguration, null);
	}

	public void initRegistration(VtnSessionConfiguration vtnConfiguration) {
		this.initRegistration(vtnConfiguration, null);
	}

	public void reinitRegistration(VtnSessionConfiguration vtnConfiguration) {
		String registrationId = (getRegistration(vtnConfiguration) != null)
				? getRegistration(vtnConfiguration).getRegistrationID()
				: null;
		clearRegistration(vtnConfiguration);
		this.initRegistration(vtnConfiguration, registrationId);
	}

	private void initRegistration(VtnSessionConfiguration vtnConfiguration, String registrationId) {

		String requestId = "0";
		OadrQueryRegistrationType queryRegistration = Oadr20bEiRegisterPartyBuilders
				.newOadr20bQueryRegistrationBuilder(requestId)
				.withSchemaVersion(SchemaVersionEnumeratedType.OADR_20B.value()).build();

		try {
			if (vtnConfiguration.getVtnUrl() != null) {
				OadrCreatedPartyRegistrationType oadrQueryRegistrationType = multiVtnConfig
						.getMultiHttpClientConfig(vtnConfiguration).oadrQueryRegistrationType(queryRegistration);
				String handle = this.handle(vtnConfiguration, oadrQueryRegistrationType, venConfig.getXmlSignature());
			} else {
				multiVtnConfig.getMultiXmppClientConfig(vtnConfiguration).oadrQueryRegistrationType(queryRegistration);
			}

		} catch (XmppStringprepException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (NotConnectedException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (Oadr20bException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (Oadr20bHttpLayerException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (Oadr20bXMLSignatureException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (Oadr20bXMLSignatureValidationException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (Oadr20bMarshalException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (InterruptedException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (OadrSecurityException e) {
			LOGGER.error("Fail to query registration", e);
		} catch (IOException e) {
			LOGGER.error("Fail to query registration", e);
		}

	}

	public void register(VtnSessionConfiguration vtnConfiguration, OadrCreatedPartyRegistrationType registration)
			throws Oadr20bMarshalException, IOException {
		setRegistration(vtnConfiguration, registration);

		LOGGER.info("Ven has successfully register using registrationId: " + registration.getRegistrationID());
		LOGGER.debug("        xmlSignature: " + vtnConfiguration.getVenSessionConfig().getXmlSignature());
		LOGGER.debug("        reportOnly  : " + vtnConfiguration.getVenSessionConfig().getReportOnly());
		LOGGER.debug("        pullModel   : " + vtnConfiguration.getVenSessionConfig().getPullModel());
		if (getListeners() != null) {
			final OadrCreatedPartyRegistrationType reg = registration;
			getListeners().forEach(listener -> listener.onRegistrationSuccess(vtnConfiguration, reg));
		}
	}

	public OadrCreatedPartyRegistrationType getRegistration(VtnSessionConfiguration vtnConfiguration) {
		return registration.get(vtnConfiguration.getVtnId());
	}

	private void setRegistration(VtnSessionConfiguration vtnConfiguration,
			OadrCreatedPartyRegistrationType registration) {
		if (registration == null) {
			this.registration.remove(vtnConfiguration.getVtnId());
		} else {
			this.registration.put(vtnConfiguration.getVtnId(), registration);
		}
	}

	public void addListener(Oadr20bVENEiRegisterPartyServiceListener listener) {
		if (getListeners() == null) {
			setListeners(new ArrayList<Oadr20bVENEiRegisterPartyServiceListener>());
		}

		if (!getListeners().contains(listener)) {
			getListeners().add(listener);
		}

	}

	public String handle(VtnSessionConfiguration vtnConfig, String raw, OadrPayload oadrPayload)
			throws Oadr20bXMLSignatureValidationException, Oadr20bMarshalException, Oadr20bApplicationLayerException,
			Oadr20bXMLSignatureException, OadrSecurityException, IOException {
		xmlSignatureService.validate(raw, oadrPayload, vtnConfig);

		if (oadrPayload.getOadrSignedObject().getOadrCancelPartyRegistration() != null) {
			LOGGER.info(vtnConfig.getVtnId() + " - OadrCancelPartyRegistrationType signed");
			return handle(vtnConfig, oadrPayload.getOadrSignedObject().getOadrCancelPartyRegistration(), true);
		} else if (oadrPayload.getOadrSignedObject().getOadrRequestReregistration() != null) {
			LOGGER.info(vtnConfig.getVtnId() + " - OadrRequestReregistrationType signed");
			return handle(vtnConfig, oadrPayload.getOadrSignedObject().getOadrRequestReregistration(), true);
		} else if (oadrPayload.getOadrSignedObject().getOadrCreatedPartyRegistration() != null) {
			LOGGER.info(vtnConfig.getVtnId() + " - OadrCreatedPartyRegistrationType signed");
			return handle(vtnConfig, oadrPayload.getOadrSignedObject().getOadrCreatedPartyRegistration(), true);
		}

		throw new Oadr20bApplicationLayerException("Unacceptable request payload for EiEventService");
	}

	public String handle(VtnSessionConfiguration vtnConfig, OadrRequestReregistrationType oadrRequestReregistrationType,
			boolean signed) throws Oadr20bMarshalException, Oadr20bXMLSignatureException, OadrSecurityException {

		OadrResponseType response = this.oadrRequestReregistration(vtnConfig, oadrRequestReregistrationType);

		String responseStr = null;

		if (signed) {
			responseStr = xmlSignatureService.sign(response, vtnConfig);
		} else {
			responseStr = jaxbContext.marshalRoot(response);
		}

		return responseStr;

	}

	public String handle(VtnSessionConfiguration vtnConfig,
			OadrCancelPartyRegistrationType oadrCancelPartyRegistrationType, boolean signed)
			throws Oadr20bMarshalException, Oadr20bXMLSignatureException, OadrSecurityException {

		OadrCanceledPartyRegistrationType response = this.oadrCancelPartyRegistration(vtnConfig,
				oadrCancelPartyRegistrationType);

		String responseStr = null;

		if (signed) {
			responseStr = xmlSignatureService.sign(response, vtnConfig);
		} else {
			responseStr = jaxbContext.marshalRoot(response);
		}

		return responseStr;

	}

	public String handle(VtnSessionConfiguration vtnConfig,
			OadrCreatedPartyRegistrationType oadrCreatedPartyRegistrationType, boolean signed)
			throws Oadr20bMarshalException, Oadr20bXMLSignatureException, OadrSecurityException, IOException {

		if (getRegistration(vtnConfig) != null && getRegistration(vtnConfig).getRegistrationID() != null
				&& getRegistration(vtnConfig).getRegistrationID()
						.equals(oadrCreatedPartyRegistrationType.getRegistrationID())) {
			return null;
		} else if (oadrCreatedPartyRegistrationType.getRegistrationID() != null) {
			this.register(vtnConfig, oadrCreatedPartyRegistrationType);
		}

		String venName = vtnConfig.getVenSessionConfig().getVenName();

		boolean xmlSignature = vtnConfig.getVenSessionConfig().getXmlSignature();
		boolean reportOnly = vtnConfig.getVenSessionConfig().getReportOnly();
		String requestId = "";
		Oadr20bCreatePartyRegistrationBuilder builder = Oadr20bEiRegisterPartyBuilders
				.newOadr20bCreatePartyRegistrationBuilder(requestId, vtnConfig.getVenSessionConfig().getVenId(),
						SchemaVersionEnumeratedType.OADR_20B.value())
				.withRegistrationId(oadrCreatedPartyRegistrationType.getRegistrationID());

		if (vtnConfig.getVtnUrl() != null) {

			Boolean pullModel = vtnConfig.getVenSessionConfig().getPullModel();
			String transportAddress = null;
			if (!pullModel) {
				transportAddress = vtnConfig.getVenSessionConfig().getVenUrl();
			}

			OadrTransportType transportType = OadrTransportType.SIMPLE_HTTP;

			builder.withOadrHttpPullModel(pullModel).withOadrTransportAddress(transportAddress)
					.withOadrReportOnly(reportOnly).withOadrTransportName(transportType).withOadrVenName(venName)
					.withOadrXmlSignature(xmlSignature);

//			if (getListeners() != null) {
//				getListeners().forEach(listener -> listener.onRegistrationError(vtnConfig));
//			}

		} else if (vtnConfig.getVtnXmppHost() != null && vtnConfig.getVtnXmppPort() != null) {

			OadrXmppVenClient20b xmppClient = multiVtnConfig.getMultiXmppClientConfig(vtnConfig);

			String transportAddress = xmppClient.getConnectionJid().toString();

			OadrTransportType transportType = OadrTransportType.XMPP;

			builder.withOadrTransportAddress(transportAddress).withOadrReportOnly(reportOnly)
					.withOadrTransportName(transportType).withOadrVenName(venName).withOadrXmlSignature(xmlSignature);

		}

		OadrCreatePartyRegistrationType createPartyRegistration = builder.build();

		try {
//			multiVtnConfig.oadrCreatePartyRegistration(vtnConfig, createPartyRegistration);

			if (vtnConfig.getVtnUrl() != null) {
				OadrCreatedPartyRegistrationType oadrQueryRegistrationType = multiVtnConfig
						.getMultiHttpClientConfig(vtnConfig).oadrCreatePartyRegistration(createPartyRegistration);
				this.handle(vtnConfig, oadrQueryRegistrationType, false);
			} else {
				multiVtnConfig.getMultiXmppClientConfig(vtnConfig).oadrCreatePartyRegistration(createPartyRegistration);
			}

		} catch (NotConnectedException e) {
			LOGGER.error("", e);
		} catch (Oadr20bException e) {
			LOGGER.error("", e);
		} catch (Oadr20bHttpLayerException e) {
			LOGGER.error("", e);
		} catch (Oadr20bXMLSignatureValidationException e) {
			LOGGER.error("", e);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		}

		return null;

	}

	public String request(String username, String payload)
			throws Oadr20bMarshalException, Oadr20bUnmarshalException, Oadr20bApplicationLayerException,
			Oadr20bXMLSignatureValidationException, Oadr20bXMLSignatureException, OadrSecurityException, IOException {

		Object unmarshal = jaxbContext.unmarshal(payload);

		VtnSessionConfiguration vtnConfig = multiVtnConfig.getMultiConfig(username);

		if (unmarshal instanceof OadrPayload) {

			OadrPayload oadrPayload = (OadrPayload) unmarshal;

			return handle(vtnConfig, payload, oadrPayload);

		} else if (unmarshal instanceof OadrRequestReregistrationType) {

			OadrRequestReregistrationType oadrRequestReregistrationType = (OadrRequestReregistrationType) unmarshal;

			LOGGER.info(username + " - OadrRequestReregistrationType");

			return handle(vtnConfig, oadrRequestReregistrationType, false);

		} else if (unmarshal instanceof OadrCancelPartyRegistrationType) {

			OadrCancelPartyRegistrationType oadrCancelPartyRegistrationType = (OadrCancelPartyRegistrationType) unmarshal;

			LOGGER.info(username + " - OadrCancelPartyRegistrationType");

			return handle(vtnConfig, oadrCancelPartyRegistrationType, false);

		} else if (unmarshal instanceof OadrCreatedPartyRegistrationType) {

			OadrCreatedPartyRegistrationType oadrCreatedPartyRegistrationType = (OadrCreatedPartyRegistrationType) unmarshal;

			LOGGER.info(username + " - OadrCancelPartyRegistrationType");

			return handle(vtnConfig, oadrCreatedPartyRegistrationType, false);

		}

		throw new Oadr20bApplicationLayerException("Unacceptable request payload for EiEventService");
	}

	public List<Oadr20bVENEiRegisterPartyServiceListener> getListeners() {
		return listeners;
	}

	private void setListeners(List<Oadr20bVENEiRegisterPartyServiceListener> listeners) {
		this.listeners = listeners;
	}

}
