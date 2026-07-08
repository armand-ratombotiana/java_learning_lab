package com.security11;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class SamlRequestBuilder {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String buildAuthnRequest(String issuer, String acsUrl, String destination) {
        String requestId = "_" + UUID.randomUUID();
        String issueInstant = java.time.Instant.now().toString();
        return String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <samlp:AuthnRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
                xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
                ID="%s" Version="2.0" IssueInstant="%s"
                Destination="%s" AssertionConsumerServiceURL="%s"
                ProtocolBinding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST">
                <saml:Issuer>%s</saml:Issuer>
                <samlp:NameIDPolicy Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"
                    AllowCreate="true"/>
            </samlp:AuthnRequest>
            """, requestId, issueInstant, destination, acsUrl, issuer);
    }

    public String buildMetadata(String entityId, String acsUrl, String logoutUrl, X509Certificate cert) {
        String certB64 = Base64.getEncoder().encodeToString(cert.getEncoded());
        return String.format("""
            <?xml version="1.0"?>
            <md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata"
                entityID="%s">
                <md:SPSSODescriptor protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
                    <md:KeyDescriptor use="signing">
                        <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                            <ds:X509Data><ds:X509Certificate>%s</ds:X509Certificate></ds:X509Data>
                        </ds:KeyInfo>
                    </md:KeyDescriptor>
                    <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                        Location="%s" index="0"/>
                    <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"
                        Location="%s"/>
                </md:SPSSODescriptor>
            </md:EntityDescriptor>
            """, entityId, certB64, acsUrl, logoutUrl);
    }

    static class X509Certificate {
        private final byte[] encoded;
        X509Certificate(byte[] encoded) { this.encoded = encoded; }
        byte[] getEncoded() { return encoded; }
    }

    public static void main(String[] args) {
        SamlRequestBuilder builder = new SamlRequestBuilder();
        String authnRequest = builder.buildAuthnRequest(
            "https://sp.example.com/saml/metadata",
            "https://sp.example.com/saml/acs",
            "https://idp.example.com/saml/sso");
        System.out.println("AuthnRequest:\n" + authnRequest);
    }
}
