package com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import java.util.concurrent.CompletableFuture;

public class MainServer {

    public static void main(String[] args) throws Exception {
        OpcUaServerConfig config = OpcUaServerConfig.builder()
            .setApplicationName(LocalizedText.english("OPC-UA Weather Server"))
            .setApplicationUri("urn:example:opcua:server")
            .setBindAddresses("0.0.0.0") // para aceitar conexões externas
            .setBindPort(4840)
            .build();

        OpcUaServer server = new OpcUaServer(config);

        // Registra o namespace
        server.getNamespaceManager().registerAndAdd(new ExampleNamespace(server));

        server.startup().get();
        System.out.println("✅ OPC-UA Server iniciado em: opc.tcp://localhost:4840");

        // Mantém o servidor ativo
        Thread.currentThread().join();
    }
}
