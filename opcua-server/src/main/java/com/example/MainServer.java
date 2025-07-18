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
                .setBindAddresses("0.0.0.0")          // Versão 0.6.8 usa setBindAddresses (lista) em vez de setBindPort direto
                .setBindPort(4840)
                .build();

        OpcUaServer server = new OpcUaServer(config);

        ExampleNamespace exampleNamespace = new ExampleNamespace(server);
        server.getNamespaceManager().registerAndAdd(exampleNamespace);

        server.startup().get();

        System.out.println("OPC UA Server running at opc.tcp://localhost:4840");

        // Mantém o servidor rodando indefinidamente
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.get();
    }
}
