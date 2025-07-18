package com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.concurrent.atomic.AtomicLong;

public class ExampleNamespace implements Namespace {

    private final OpcUaServer server;
    private final UShort namespaceIndex;
    private final AtomicLong nodeIdCounter = new AtomicLong();

    private final String namespaceUri = "urn:example:opcua:weather";

    public ExampleNamespace(OpcUaServer server) {
        this.server = server;
        this.namespaceIndex = server.getNamespaceTable().addUri(namespaceUri);
    }

    @Override
    public UShort getNamespaceIndex() {
        return namespaceIndex;
    }

    @Override
    public String getNamespaceUri() {
        return namespaceUri;
    }

    @Override
    public void onStartup() {
        // Cria a pasta raiz
        UaFolderNode rootNode = new UaFolderNode(
            server.getNodeMap(),
            new NodeId(namespaceIndex, "WeatherStations"),
            new QualifiedName(namespaceIndex, "WeatherStations"),
            LocalizedText.english("WeatherStations")
        );

        server.getNodeMap().addNode(rootNode);
        server.getUaNamespace().addReference(
            Identifiers.ObjectsFolder,
            Identifiers.Organizes,
            true,
            rootNode.getNodeId()
        );

        // Cria uma estação de exemplo
        createStation(rootNode, "WS_AHEAD_H_IN");
    }

    private void createStation(UaFolderNode parent, String stationName) {
        UaFolderNode stationNode = new UaFolderNode(
            server.getNodeMap(),
            new NodeId(namespaceIndex, "Station." + stationName),
            new QualifiedName(namespaceIndex, stationName),
            LocalizedText.english(stationName)
        );

        server.getNodeMap().addNode(stationNode);
        parent.addOrganizes(stationNode);

        // Variáveis da estação
        createVariable(stationNode, "temperature_C", 25.0);
        createVariable(stationNode, "humidity_percent", 50.0);
        createVariable(stationNode, "pressure_hPa", 1013.25);
    }

    private void createVariable(UaFolderNode parent, String name, double initialValue) {
        UaVariableNode node = UaVariableNode.builder(server.getNodeMap())
            .setNodeId(new NodeId(namespaceIndex, name))
            .setBrowseName(new QualifiedName(namespaceIndex, name))
            .setDisplayName(LocalizedText.english(name))
            .setDataType(Identifiers.Double)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setAccessLevel(Unsigned.ubyte(3)) // READ_WRITE
            .setUserAccessLevel(Unsigned.ubyte(3))
            .setValue(new DataValue(new Variant(initialValue)))
            .build();

        server.getNodeMap().addNode(node);
        parent.addOrganizes(node);
    }

    @Override
    public void onShutdown() {
        // cleanup opcional
    }
}
