package com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

public class MainServer {

    public static void main(String[] args) throws Exception {
        // Cria a configuração do servidor
        OpcUaServerConfig config = OpcUaServerConfig.builder()
            .setApplicationName(LocalizedText.english("Weather Station OPC UA Server"))
            .setApplicationUri("urn:eclipse:milo:weatherstation")
            .setBindPort(4840)
            .build();

        OpcUaServer server = new OpcUaServer(config);

        // Registra o namespace personalizado
        String namespaceUri = "urn:opcua:weatherstation";
        ExampleNamespace namespace = new ExampleNamespace(server, namespaceUri);
        server.getNamespaceManager().registerNamespace(namespace);

        server.startup().get(); // inicia o servidor
        System.out.println("OPC-UA Server started at: opc.tcp://localhost:4840");

        Thread.currentThread().join(); // mantém o processo vivo
    }
}
