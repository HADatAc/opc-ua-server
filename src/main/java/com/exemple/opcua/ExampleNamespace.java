package com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.FolderTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.Identifiers;

import java.util.concurrent.atomic.AtomicLong;

public class ExampleNamespace implements Namespace {

    private final OpcUaServer server;
    private final String namespaceUri;
    private final UShort namespaceIndex;
    private final AtomicLong nodeIdCounter = new AtomicLong(1);
    private final SubscriptionModel subscriptionModel;

    public ExampleNamespace(OpcUaServer server, String namespaceUri) {
        this.server = server;
        this.namespaceUri = namespaceUri;
        this.namespaceIndex = server.getNamespaceManager().registerAndAddUri(namespaceUri);
        this.subscriptionModel = new SubscriptionModel(server, this);
    }

    @Override
    public String getNamespaceUri() {
        return namespaceUri;
    }

    @Override
    public void onStartup() {
        UaFolderNode rootFolder = new UaFolderNode(
            server.getNodeMap(),
            new NodeId(namespaceIndex, "WSFolder"),
            new QualifiedName(namespaceIndex, "WSFolder"),
            LocalizedText.english("WeatherStations")
        );

        server.getNodeMap().addNode(rootFolder);
        rootFolder.addReference(new Reference(
            rootFolder.getNodeId(),
            Identifiers.Organizes,
            Identifiers.ObjectsFolder.expanded(),
            false
        ));

        addWeatherStation(rootFolder, "WS_Ahead_H_IN");
    }

    private void addWeatherStation(UaFolderNode parent, String stationName) {
        NodeId stationId = new NodeId(namespaceIndex, "Station." + stationName);
        UaFolderNode stationNode = new UaFolderNode(
            server.getNodeMap(),
            stationId,
            new QualifiedName(namespaceIndex, stationName),
            LocalizedText.english(stationName)
        );

        server.getNodeMap().addNode(stationNode);
        parent.addOrganizes(stationNode);

        createVariable(stationNode, "temperature_dht11_C", 30.0);
        createVariable(stationNode, "humidity_dht11_percent", 32.0);
        createVariable(stationNode, "air_quality_V", 0.43);
        createVariable(stationNode, "pressure_bme", 1012.5);
    }

    private void createVariable(UaFolderNode parent, String name, Object initialValue) {
        NodeId variableId = new NodeId(namespaceIndex, name);

        UaVariableNode variableNode = UaVariableNode.builder(server.getNodeMap())
            .setNodeId(variableId)
            .setBrowseName(new QualifiedName(namespaceIndex, name))
            .setDisplayName(LocalizedText.english(name))
            .setDataType(Identifiers.Double)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setValue(new DataValue(new Variant(initialValue)))
            .setAccessLevel(Unsigned.ubyte(AccessLevel.getMask(AccessLevel.READ_WRITE)))
            .build();

        server.getNodeMap().addNode(variableNode);
        parent.addOrganizes(variableNode);
    }

    @Override
    public void onShutdown() {
        subscriptionModel.cleanup();
    }
}
