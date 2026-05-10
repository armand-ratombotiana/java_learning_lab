package com.learning.servicemesh;

import io.istio.io.v1alpha1.api.IstioApi;
import io.istio.io.v1alpha1.models.*;

import java.util.HashMap;
import java.util.Map;

public class ServiceMeshSolution {

    public VirtualService createVirtualService(String name, String host, String destination, String subset) {
        VirtualService vs = new VirtualService();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        vs.setMetadata(metadata);

        VirtualServiceSpec spec = new VirtualServiceSpec();
        spec.setHosts(java.util.List.of(host));

        HTTPRoute destinationRule = new HTTPRoute();
        destinationRule.setDestination(new Destination().setHost(destination).setSubset(subset));
        spec.setHttp(java.util.List.of(destinationRule));

        vs.setSpec(spec);
        return vs;
    }

    public DestinationRule createDestinationRule(String name, String host, String subset, String label) {
        DestinationRule dr = new DestinationRule();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        dr.setMetadata(metadata);

        DestinationRuleSpec spec = new DestinationRuleSpec();
        spec.setHost(host);

        Subset subsetObj = new Subset();
        subsetObj.setName(subset);
        subsetObj.setLabels(Map.of("version", label));
        spec.setSubsets(java.util.List.of(subsetObj));

        dr.setSpec(spec);
        return dr;
    }

    public Gateway createGateway(String name, String selector, int port) {
        Gateway gw = new Gateway();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        gw.setMetadata(metadata);

        GatewaySpec spec = new GatewaySpec();
        spec.setSelector(Map.of("istio", selector));

        Server server = new Server();
        ServerPort serverPort = new ServerPort();
        serverPort.setNumber(port);
        serverPort.setProtocol("HTTP");
        server.setPort(serverPort);
        spec.setServers(java.util.List.of(server));

        gw.setSpec(spec);
        return gw;
    }

    public Sidecar createSidecar(String name, String namespace) {
        Sidecar sc = new Sidecar();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        metadata.setNamespace(namespace);
        sc.setMetadata(metadata);

        SidecarSpec spec = new SidecarSpec();
        sc.setSpec(spec);
        return sc;
    }

    public AuthorizationPolicy createAuthorizationPolicy(String name, String namespace, String action) {
        AuthorizationPolicy ap = new AuthorizationPolicy();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        metadata.setNamespace(namespace);
        ap.setMetadata(metadata);

        AuthorizationPolicySpec spec = new AuthorizationPolicySpec();
        spec.setAction(action);
        ap.setSpec(spec);
        return ap;
    }

    public PeerAuthentication createPeerAuthentication(String name, String namespace, String mode) {
        PeerAuthentication pa = new PeerAuthentication();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        metadata.setNamespace(namespace);
        pa.setMetadata(metadata);

        PeerAuthenticationSpec spec = new PeerAuthenticationSpec();
        spec.setMtls(new MtlsSettings().setMode(mode));
        pa.setSpec(spec);
        return pa;
    }
}

class V1ObjectMeta {
    private String name;
    private String namespace;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
}

class VirtualService {
    private V1ObjectMeta metadata;
    private VirtualServiceSpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public VirtualServiceSpec getSpec() { return spec; }
    public void setSpec(VirtualServiceSpec s) { this.spec = s; }
}

class VirtualServiceSpec {
    private java.util.List<String> hosts;
    private java.util.List<HTTPRoute> http;

    public java.util.List<String> getHosts() { return hosts; }
    public void setHosts(java.util.List<String> h) { this.hosts = h; }
    public java.util.List<HTTPRoute> getHttp() { return http; }
    public void setHttp(java.util.List<HTTPRoute> h) { this.http = h; }
}

class HTTPRoute {
    private Destination destination;

    public Destination getDestination() { return destination; }
    public void setDestination(Destination d) { this.destination = d; }
}

class Destination {
    private String host;
    private String subset;

    public String getHost() { return host; }
    public void setHost(String h) { this.host = h; }
    public String getSubset() { return subset; }
    public void setSubset(String s) { this.subset = s; }
}

class DestinationRule {
    private V1ObjectMeta metadata;
    private DestinationRuleSpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public DestinationRuleSpec getSpec() { return spec; }
    public void setSpec(DestinationRuleSpec s) { this.spec = s; }
}

class DestinationRuleSpec {
    private String host;
    private java.util.List<Subset> subsets;

    public String getHost() { return host; }
    public void setHost(String h) { this.host = h; }
    public java.util.List<Subset> getSubsets() { return subsets; }
    public void setSubsets(java.util.List<Subset> s) { this.subsets = s; }
}

class Subset {
    private String name;
    private Map<String, String> labels;

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> l) { this.labels = l; }
}

class Gateway {
    private V1ObjectMeta metadata;
    private GatewaySpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public GatewaySpec getSpec() { return spec; }
    public void setSpec(GatewaySpec s) { this.spec = s; }
}

class GatewaySpec {
    private Map<String, String> selector;
    private java.util.List<Server> servers;

    public Map<String, String> getSelector() { return selector; }
    public void setSelector(Map<String, String> s) { this.selector = s; }
    public java.util.List<Server> getServers() { return servers; }
    public void setServers(java.util.List<Server> s) { this.servers = s; }
}

class Server {
    private ServerPort port;

    public ServerPort getPort() { return port; }
    public void setPort(ServerPort p) { this.port = p; }
}

class ServerPort {
    private int number;
    private String protocol;

    public int getNumber() { return number; }
    public void setNumber(int n) { this.number = n; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String p) { this.protocol = p; }
}

class Sidecar {
    private V1ObjectMeta metadata;
    private SidecarSpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public SidecarSpec getSpec() { return spec; }
    public void setSpec(SidecarSpec s) { this.spec = s; }
}

class SidecarSpec {}

class AuthorizationPolicy {
    private V1ObjectMeta metadata;
    private AuthorizationPolicySpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public AuthorizationPolicySpec getSpec() { return spec; }
    public void setSpec(AuthorizationPolicySpec s) { this.spec = s; }
}

class AuthorizationPolicySpec {
    private String action;

    public String getAction() { return action; }
    public void setAction(String a) { this.action = a; }
}

class PeerAuthentication {
    private V1ObjectMeta metadata;
    private PeerAuthenticationSpec spec;

    public V1ObjectMeta getMetadata() { return metadata; }
    public void setMetadata(V1ObjectMeta m) { this.metadata = m; }
    public PeerAuthenticationSpec getSpec() { return spec; }
    public void setSpec(PeerAuthenticationSpec s) { this.spec = s; }
}

class PeerAuthenticationSpec {
    private MtlsSettings mtls;

    public MtlsSettings getMtls() { return mtls; }
    public void setMtls(MtlsSettings m) { this.mtls = m; }
}

class MtlsSettings {
    private String mode;

    public String getMode() { return mode; }
    public void setMode(String m) { this.mode = m; }
}