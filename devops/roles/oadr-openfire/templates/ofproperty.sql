INSERT INTO ofproperty(name,propvalue) VALUES('admin.web.ssl.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('admin.web.ssl.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('admin.web.ssl.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('admin.web.ssl.truststore','/usr/share/openfire/resources/security/truststore');

INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.certificate.accept-selfsigned','false');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.certificate.verify.validity','true');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.ciphersuites','TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDH_RSA_WITH_AES_256_CBC_SHA,TLS_DHE_RSA_WITH_AES_256_CBC_SHA,TLS_DHE_DSS_WITH_AES_256_CBC_SHA,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256,TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,TLS_DHE_DSS_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA,TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA,TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_DSS_WITH_AES_128_CBC_SHA,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384,TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,TLS_DHE_DSS_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256,TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,TLS_DHE_DSS_WITH_AES_128_GCM_SHA256,TLS_EMPTY_RENEGOTIATION_INFO_SCSV');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.protocols','TLSv1.2');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.client.truststore','/usr/share/openfire/resources/security/client.truststore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.socket.ssl.truststore','/usr/share/openfire/resources/security/truststore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.component.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.component.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.component.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.component.truststore','/usr/share/openfire/resources/security/truststore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.multiplex.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.multiplex.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.multiplex.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.multiplex.truststore','/usr/share/openfire/resources/security/truststore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.bosh.ssl.client.keypass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.bosh.ssl.client.keystore','/usr/share/openfire/resources/security/keystore');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.bosh.ssl.client.trustpass','changeme');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.bosh.ssl.client.truststore','/usr/share/openfire/resources/security/client.truststore');
# INSERT INTO ofproperty(name,propvalue) VALUES('hybridAuthProvider.primaryProvider.className','org.jivesoftware.openfire.auth.DefaultAuthProvider');
# INSERT INTO ofproperty(name,propvalue) VALUES('hybridAuthProvider.secondaryProvider.className','com.avob.server.openfire.OpenfireOadrAuthProvider');
# INSERT INTO ofproperty(name,propvalue) VALUES('provider.auth.className','org.jivesoftware.openfire.auth.HybridAuthProvider');
INSERT INTO ofproperty(name,propvalue) VALUES('provider.auth.className','org.jivesoftware.openfire.auth.DefaultAuthProvider');
INSERT INTO ofproperty(name,propvalue) VALUES('sasl.mechs','ANONYMOUS');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.auth.anonymous','true');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.auth.external','true');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.client.cert.policy','wanted');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.client.tls.policy','required');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.oadr.vtnAuthEndpoint','https://vtn.oadr.com:8181/testvtn/Role');
INSERT INTO ofproperty(name,propvalue) VALUES('xmpp.oadr.vtnId','810fa6a7f45089d1e00e');
