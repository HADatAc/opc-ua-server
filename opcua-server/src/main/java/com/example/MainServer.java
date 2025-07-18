package com.example;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import java.util.concurrent.CompletableFuture;

public class MainServer {

    public static void main(String[] args) throws Exception {
        OpcUaServerConfig config = OpcUaServerConfig.builder()
            .setApplicationName(LocalizedText.english("Weather Station OPC-UA Server"))
            .setApplicationUri("urn:example:weatherstation:server")
            .setBindPort(12686)
            .build();

        OpcUaServer server = new OpcUaServer(config);

        ExampleNamespace exampleNamespace = new ExampleNamespace(server);

        server.getNamespaceManager().registerAndAdd(exampleNamespace).get();

        server.startup().get();

        System.out.println("OPC UA Server running at opc.tcp://localhost:12686");

        // Keep server running indefinitely
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.get();
    }
}
