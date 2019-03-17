package com.avob.openadr.server.common.vtn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.avob.openadr.server.common.vtn.ApplicationTest;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEvent;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventOptEnum;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventSimpleValueEnum;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventStateEnum;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.dto.DemandResponseEventDto;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.dto.DemandResponseEventSignalDto;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.dto.DemandResponseEventTargetDto;
import com.avob.openadr.server.common.vtn.models.ven.Ven;
import com.avob.openadr.server.common.vtn.models.vendemandresponseevent.VenDemandResponseEvent;
import com.avob.openadr.server.common.vtn.models.vendemandresponseevent.VenDemandResponseEventDao;
import com.avob.openadr.server.common.vtn.models.venmarketcontext.VenMarketContext;
import com.avob.openadr.server.common.vtn.models.venmarketcontext.VenMarketContextDto;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTest.class })
@WebAppConfiguration
public class DemandResponseEventServiceTest {

	@Resource
	private DemandResponseEventService demandResponseEventService;

	@Resource
	private VenService venService;

	@Resource
	private VenDemandResponseEventDao venDemandResponseEventDao;

	@Resource
	private VenMarketContextService venMarketContextService;

	private Long start = System.currentTimeMillis();
	private List<Ven> vens = new ArrayList<Ven>();
	private List<DemandResponseEvent> events = new ArrayList<DemandResponseEvent>();
	private VenMarketContext marketContext = null;
	private DemandResponseEvent event1 = null;
	private DemandResponseEvent event2 = null;
	private DemandResponseEvent event3 = null;

	private String event1Id = "event1Id";
	private String event2Id = "event2Id";
	private String event3Id = "event3Id";

	private Ven ven1;
	private Ven ven2;
	private Ven ven3;

	@Before
	public void before() {

		String marketContextName = "http://oadr.avob.com";
		marketContext = venMarketContextService.prepare(new VenMarketContextDto(marketContextName));
		venMarketContextService.save(marketContext);

		ven1 = venService.prepare("ven1");
		ven1.setVenMarketContexts(Sets.newHashSet(marketContext));
		ven1 = venService.save(ven1);
		vens.add(ven1);

		ven2 = venService.prepare("ven2");
		ven2.setVenMarketContexts(Sets.newHashSet(marketContext));
		ven2 = venService.save(ven2);
		vens.add(ven2);

		ven3 = venService.prepare("ven3");
		ven3.setVenMarketContexts(Sets.newHashSet(marketContext));
		ven3 = venService.save(ven3);
		vens.add(ven3);

		Long createdTimestamp = 0L;
		String duration = "PT1H";
		String notificationDuration = "P1D";

		DemandResponseEventStateEnum state = DemandResponseEventStateEnum.ACTIVE;

		DemandResponseEventSignalDto signal = new DemandResponseEventSignalDto();
		signal.setCurrentValue(DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_HIGH.getValue());
		signal.setSignalName("SIMPLE");
		signal.setSignalType("level");

		DemandResponseEventDto dto = new DemandResponseEventDto();
		dto.setEventId(event1Id);
		dto.setState(state);
		dto.getActivePeriod().setStart(start);
		dto.getDescriptor().setMarketContext(marketContext.getName());
		dto.getActivePeriod().setDuration(duration);
		dto.getActivePeriod().setNotificationDuration(notificationDuration);
		dto.setCreatedTimestamp(createdTimestamp);
		dto.getTargets().add(new DemandResponseEventTargetDto("ven", "ven1"));
		dto.getTargets().add(new DemandResponseEventTargetDto("ven", "ven2"));
		dto.getSignals().add(signal);
		event1 = demandResponseEventService.create(dto);
		events.add(event1);

		dto = new DemandResponseEventDto();
		dto.setEventId(event2Id);
		dto.setState(DemandResponseEventStateEnum.CANCELED);
		dto.getActivePeriod().setStart(start);
		dto.getDescriptor().setMarketContext(marketContext.getName());
		dto.getActivePeriod().setDuration(duration);
		dto.getActivePeriod().setNotificationDuration(notificationDuration);
		dto.setCreatedTimestamp(createdTimestamp);
		dto.getActivePeriod().setRampUpDuration("PT1H");
		dto.getActivePeriod().setRecoveryDuration("PT1H");
		dto.getDescriptor().setVtnComment("comment");
		dto.getTargets().add(new DemandResponseEventTargetDto("ven", "ven2"));
		dto.getSignals().add(signal);
		event2 = demandResponseEventService.create(dto);
		events.add(event2);

		dto = new DemandResponseEventDto();
		dto.setEventId(event3Id);
		dto.setState(state);
		dto.getActivePeriod().setStart(start);
		dto.getDescriptor().setMarketContext(marketContext.getName());
		dto.getActivePeriod().setDuration(duration);
		dto.getActivePeriod().setNotificationDuration(notificationDuration);
		dto.setCreatedTimestamp(createdTimestamp);
		dto.getTargets().add(new DemandResponseEventTargetDto("ven", "ven3"));
		dto.getSignals().add(signal);
		event3 = demandResponseEventService.create(dto);
		events.add(event3);
	}

	@After
	public void after() {
		demandResponseEventService.delete(events);
		venService.delete(vens);
		venMarketContextService.delete(marketContext);
	}

	@Test
	public void findTest() {
		List<DemandResponseEvent> find = demandResponseEventService.find(null, null);
		assertEquals(3, find.size());

		find = demandResponseEventService.find(null, null, 1L);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven2", null);
		assertEquals(2, find.size());

		find = demandResponseEventService.find("ven2", DemandResponseEventStateEnum.ACTIVE);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven2", DemandResponseEventStateEnum.CANCELED);
		assertEquals(1, find.size());
		assertEquals("PT1H", find.get(0).getActivePeriod().getRampUpDuration());
		assertEquals("PT1H", find.get(0).getActivePeriod().getRecoveryDuration());
		assertEquals("comment", find.get(0).getDescriptor().getVtnComment());
		// duration is 1h long
		Long end = start + 60 * 60 * 1000;
		assertEquals(end, find.get(0).getActivePeriod().getEnd());

		find = demandResponseEventService.find(null, DemandResponseEventStateEnum.ACTIVE);
		assertEquals(2, find.size());

		find = demandResponseEventService.find(null, DemandResponseEventStateEnum.ACTIVE, 1L);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven1", null);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven1", DemandResponseEventStateEnum.ACTIVE);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven1", DemandResponseEventStateEnum.ACTIVE, 2L);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven2", null, 1L);
		assertEquals(1, find.size());

		find = demandResponseEventService.find("ven3", null, 1L);
		assertEquals(1, find.size());
	}

	@Test
	public void findtoSentEvent() {
		List<DemandResponseEvent> findToSentEvent = demandResponseEventService.findToSentEventByVen(ven1);
		assertEquals(1, findToSentEvent.size());

		findToSentEvent = demandResponseEventService.findToSentEventByVen(ven1, 1L);
		assertEquals(1, findToSentEvent.size());

		findToSentEvent = demandResponseEventService.findToSentEventByVenUsername(ven1.getUsername());
		assertEquals(1, findToSentEvent.size());

		findToSentEvent = demandResponseEventService.findToSentEventByVenUsername(ven1.getUsername(), 1L);
		assertEquals(1, findToSentEvent.size());
	}

	@Test
	public void createTest() {
		long count = venDemandResponseEventDao.count();
		assertEquals(4L, count);

		Iterable<VenDemandResponseEvent> findAll = venDemandResponseEventDao.findAll();

		Iterator<VenDemandResponseEvent> iterator = findAll.iterator();

		while (iterator.hasNext()) {
			VenDemandResponseEvent next = iterator.next();
			assertEquals(-1, next.getLastSentModificationNumber());
			assertNull(next.getVenOpt());
			assertTrue(next.getVen().getUsername() == "ven1" || next.getVen().getUsername() == "ven2"
					|| next.getVen().getUsername() == "ven3");
		}
	}

	@Test
	public void deleteTest() {
		long count = venDemandResponseEventDao.count();
		assertEquals(4L, count);

		demandResponseEventService.delete(event1.getId());

		count = venDemandResponseEventDao.count();
		assertEquals(2L, count);

		demandResponseEventService.delete(events);

		count = venDemandResponseEventDao.count();
		assertEquals(0L, count);
	}

	/**
	 * test cancel update state / modification / last update timestamp
	 * 
	 * test cancel indempotent if event already canceled
	 */
	@Test
	public void cancelTest() {
		// null should raise an exception
		boolean exception = false;
		try {
			demandResponseEventService.cancel(null);
		} catch (Exception e) {
			exception = true;
		}
		assertTrue(exception);

		// non-existent id should return null
		DemandResponseEvent updateValue = demandResponseEventService.cancel(999L);
		assertNull(updateValue);

		// event should have some properties
		DemandResponseEvent findById = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(DemandResponseEventStateEnum.ACTIVE, findById.getState());
		assertEquals(0, findById.getModificationNumber());
		assertNull(findById.getLastUpdateTimestamp());

		// cancel should update those properties
		demandResponseEventService.cancel(event1.getId());

		findById = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(DemandResponseEventStateEnum.CANCELED, findById.getState());
		assertEquals(1, findById.getModificationNumber());
		assertNotNull(findById.getLastUpdateTimestamp());

		// cancel should be indempotent on already activated event
		demandResponseEventService.cancel(event1.getId());

		DemandResponseEvent findById2 = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(DemandResponseEventStateEnum.CANCELED, findById2.getState());
		assertEquals(findById.getModificationNumber(), findById2.getModificationNumber());
		assertEquals(findById.getLastUpdateTimestamp(), findById2.getLastUpdateTimestamp());

	}

	/**
	 * test active update state / modification / last update timestamp
	 * 
	 * test active indempotent if event already canceled
	 */
	@Test
	public void activeTest() {
		// null should raise an exception
		boolean exception = false;
		try {
			demandResponseEventService.active(null);
		} catch (Exception e) {
			exception = true;
		}
		assertTrue(exception);

		// non-existent id should return null
		DemandResponseEvent updateValue = demandResponseEventService.active(999L);
		assertNull(updateValue);

		// event should have some properties
		DemandResponseEvent findById = demandResponseEventService.findById(event2.getId()).get();

		assertEquals(DemandResponseEventStateEnum.CANCELED, findById.getState());
		assertEquals(0, findById.getModificationNumber());
		assertNull(findById.getLastUpdateTimestamp());

		// active should update those properties
		demandResponseEventService.active(event2.getId());

		findById = demandResponseEventService.findById(event2.getId()).get();

		assertEquals(DemandResponseEventStateEnum.ACTIVE, findById.getState());
		assertEquals(1, findById.getModificationNumber());
		assertNotNull(findById.getLastUpdateTimestamp());

		// active should be indempotent on already activated event
		demandResponseEventService.active(event2.getId());

		DemandResponseEvent findById2 = demandResponseEventService.findById(event2.getId()).get();

		assertEquals(DemandResponseEventStateEnum.ACTIVE, findById2.getState());
		assertEquals(findById.getModificationNumber(), findById2.getModificationNumber());
		assertEquals(findById.getLastUpdateTimestamp(), findById2.getLastUpdateTimestamp());
	}

	/**
	 * test updateValue update value / modification / last update timestamp
	 * 
	 * test updateValue indempotent if event value is already set
	 */
	@Test
	public void updateValueTest() {
		// null should raise an exception
		boolean exception = false;
		try {
			demandResponseEventService.updateValue(null,
					DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue());
		} catch (Exception e) {
			exception = true;
		}
		assertTrue(exception);

		// non-existent id should return null
		DemandResponseEvent updateValue = demandResponseEventService.updateValue(999L,
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue());
		assertNull(updateValue);

		// event have some properties
		DemandResponseEvent findById = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(0, findById.getModificationNumber());
		assertNull(findById.getLastUpdateTimestamp());

		assertEquals(DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_HIGH.getValue(),
				demandResponseEventService.getSignals(findById).get(0).getCurrentValue());

		// updateValue should update those properties
		demandResponseEventService.updateValue(event1.getId(),
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue());

		findById = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(1, findById.getModificationNumber());
		assertNotNull(findById.getLastUpdateTimestamp());
		assertEquals(DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue(),
				demandResponseEventService.getSignals(findById).get(0).getCurrentValue());

		// updateValue should be indempotent on event alread set with this value
		demandResponseEventService.updateValue(event1.getId(),
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue());

		DemandResponseEvent findById2 = demandResponseEventService.findById(event1.getId()).get();

		assertEquals(findById.getModificationNumber(), findById2.getModificationNumber());
		assertEquals(findById.getLastUpdateTimestamp(), findById2.getLastUpdateTimestamp());
		assertEquals(DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL.getValue(),
				demandResponseEventService.getSignals(findById).get(0).getCurrentValue());
	}

	@Test
	public void getUpdateVenOptTest() {

		DemandResponseEventOptEnum venOpt = demandResponseEventService.getVenOpt(event1.getId(), ven1.getUsername());
		assertNull(venOpt);
		assertFalse(demandResponseEventService.hasResponded(ven1.getUsername(), event1));
		demandResponseEventService.updateVenOpt(event1.getId(), event1.getModificationNumber(), ven1.getUsername(),
				DemandResponseEventOptEnum.OPT_IN);
		assertTrue(demandResponseEventService.hasResponded(ven1.getUsername(), event1));
		venOpt = demandResponseEventService.getVenOpt(event1.getId(), ven1.getUsername());
		assertNotNull(venOpt);
		assertEquals(DemandResponseEventOptEnum.OPT_IN, venOpt);
		venOpt = demandResponseEventService.getVenOpt(event1.getId(), ven2.getUsername());
		assertNull(venOpt);

		demandResponseEventService.updateVenOpt(event1.getId(), event1.getModificationNumber(), ven2.getUsername(),
				DemandResponseEventOptEnum.OPT_OUT);
		venOpt = demandResponseEventService.getVenOpt(event1.getId(), ven2.getUsername());
		assertNotNull(venOpt);
		assertEquals(DemandResponseEventOptEnum.OPT_OUT, venOpt);
	}

}
