package com.security11;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SamlAssertionProcessor {

    private static final long CLOCK_SKEW_SECONDS = 300;
    private static final Set<String> VALIDATED_ASSERTIONS = ConcurrentHashMap.newKeySet();

    public SamlAssertion parseSamlResponse(String samlXml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(samlXml.getBytes()));
        Element assertionEl = doc.getDocumentElement();
        SamlAssertion assertion = new SamlAssertion();
        assertion.id = assertionEl.getAttribute("ID");
        assertion.issueInstant = Instant.parse(assertionEl.getAttribute("IssueInstant"));
        NodeList conditions = assertionEl.getElementsByTagName("saml:Conditions");
        if (conditions.getLength() > 0) {
            Element cond = (Element) conditions.item(0);
            assertion.notBefore = Instant.parse(cond.getAttribute("NotBefore"));
            assertion.notOnOrAfter = Instant.parse(cond.getAttribute("NotOnOrAfter"));
        }
        NodeList attrs = assertionEl.getElementsByTagName("saml:Attribute");
        for (int i = 0; i < attrs.getLength(); i++) {
            Element attr = (Element) attrs.item(i);
            String name = attr.getAttribute("Name");
            String value = attr.getElementsByTagName("saml:AttributeValue").item(0).getTextContent();
            assertion.attributes.put(name, value);
        }
        return assertion;
    }

    public boolean validateAssertion(SamlAssertion assertion) {
        Instant now = Instant.now();
        if (VALIDATED_ASSERTIONS.contains(assertion.id)) return false;
        if (assertion.notOnOrAfter != null && now.isAfter(assertion.notOnOrAfter.plusSeconds(CLOCK_SKEW_SECONDS))) return false;
        if (assertion.notBefore != null && now.isBefore(assertion.notBefore.minusSeconds(CLOCK_SKEW_SECONDS))) return false;
        if (assertion.issueInstant != null && now.isBefore(assertion.issueInstant)) return false;
        VALIDATED_ASSERTIONS.add(assertion.id);
        return true;
    }

    public boolean verifySignature(String samlXml, X509Certificate certificate) throws Exception {
        PublicKey publicKey = certificate.getPublicKey();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(samlXml.getBytes()));
        NodeList signatures = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (signatures.getLength() == 0) return false;
        Element signatureEl = (Element) signatures.item(0);
        DOMValidateContext valContext = new DOMValidateContext(publicKey, signatureEl);
        XMLSignature signature = org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI.unmarshal(
            XMLSignature.class, signatureEl);
        return signature.validate(valContext);
    }

    public Map<String, String> extractAttributes(SamlAssertion assertion) {
        return Collections.unmodifiableMap(assertion.attributes);
    }

    public static class SamlAssertion {
        String id;
        Instant issueInstant;
        Instant notBefore;
        Instant notOnOrAfter;
        String issuer;
        String subject;
        final Map<String, String> attributes = new HashMap<>();

        public String getId() { return id; }
        public String getSubject() { return subject; }
        public String getIssuer() { return issuer; }
        public Map<String, String> getAttributes() { return attributes; }
        public Instant getNotOnOrAfter() { return notOnOrAfter; }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("SAML Assertion Processor ready");
    }
}
