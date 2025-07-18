package src.main.java.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

public class MainServer {

    public static void main(String[] args) throws Exception {
        OpcUaServer server = new OpcUaServer();

        // Criar um namespace próprio
        String namespaceUri = "urn:opcua:weatherstation";
        ExampleNamespace namespace = new ExampleNamespace(server, namespaceUri);
        server.getNamespaceManager().registerAndAdd(namespace);

        server.startup().get(); // inicia servidor
        System.out.println("OPC-UA Server started at: opc.tcp://localhost:4840");

        Thread.currentThread().join(); // mantém o processo vivo
    }
}