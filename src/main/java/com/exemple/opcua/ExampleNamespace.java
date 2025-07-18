package com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.FolderTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.util.NodeUtil;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;

import java.util.concurrent.atomic.AtomicLong;

public class ExampleNamespace implements Namespace {

    private final String namespaceUri;
    private final OpcUaServer server;
    private final UShort namespaceIndex;
    private final AtomicLong nodeIdCounter = new AtomicLong(1);

    public ExampleNamespace(OpcUaServer server, String namespaceUri) {
        this.server = server;
        this.namespaceUri = namespaceUri;
        this.namespaceIndex = server.getNamespaceTable().addUri(namespaceUri);
    }

    @Override
    public UShort getNamespaceIndex() {
        return namespaceIndex;
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
        server.getUaNamespace().addReference(
            Identifiers.ObjectsFolder,
            Identifiers.Organizes,
            true,
            rootFolder.getNodeId()
        );

        // Exemplo: criar uma estação com variáveis
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

        // Criar variáveis
        createVariable(stationNode, "temperature_dht11_C", 30.0);
        createVariable(stationNode, "humidity_dht11_percent", 32.0);
        createVariable(stationNode, "air_quality_V", 0.43);
        createVariable(stationNode, "pressure_bme", 1012.5);
    }

    private void createVariable(UaFolderNode parent, String name, Object initialValue) {
        NodeId variableId = new NodeId(namespaceIndex, name);
        QualifiedName browseName = new QualifiedName(namespaceIndex, name);
        LocalizedText displayName = LocalizedText.english(name);

        VariableNode variableNode = NodeUtil.createVariableNode(
            server.getNodeMap(),
            variableId,
            browseName,
            displayName,
            new Variant(initialValue)
        );

        server.getNodeMap().addNode(variableNode);
        parent.addOrganizes(variableNode);
    }
}
