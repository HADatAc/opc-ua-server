package src.main.java.com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import java.util.concurrent.CompletableFuture;

public class MainServer {

    public static void main(String[] args) throws Exception {
        OpcUaServerConfig config = OpcUaServerConfig.builder()
            .setApplicationName(LocalizedText.english("Eclipse Milo OPC UA Server"))
            .setApplicationUri("urn:eclipse:milo:examples:server")
            .setBindAddresses(new String[]{"0.0.0.0"})
            .setBindPort(12686)
            .setBuildInfo(null)
            .build();

        OpcUaServer server = new OpcUaServer(config);

        Namespace exampleNamespace = new ExampleNamespace(server);
        server.getNamespaceTable().addUri(exampleNamespace.getNamespaceUri());

        server.startup().get();

        System.out.println("OPC UA Server is running at opc.tcp://localhost:12686");

        // Keep running
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.get();
    }
}
