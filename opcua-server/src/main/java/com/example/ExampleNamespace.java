package com.example;

import org.eclipse.milo.opcua.sdk.server.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;

public class ExampleNamespace extends ManagedNamespace {

    public ExampleNamespace(OpcUaServer server) {
        super(server, "urn:example:weatherstation:namespace");
        createNodes();
    }

    private void createNodes() {
        UaFolderNode folderNode = new UaFolderNode(
            getNodeContext(),
            new NodeId(getNamespaceIndex(), "MyObjects"),
            LocalizedText.english("MyObjects")
        );

        getNodeManager().addNode(folderNode);

        folderNode.addReference(
            Identifiers.ObjectsFolder,
            Identifiers.Organizes,
            folderNode.getNodeId().expanded(),
            false
        );

        UaVariableNode temperatureNode = UaVariableNode.builder(getNodeContext())
            .setNodeId(new NodeId(getNamespaceIndex(), "Temperature"))
            .setBrowseName("Temperature")
            .setDisplayName(LocalizedText.english("Temperature"))
            .setDataType(Identifiers.Double)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setValue(new DataValue(new Variant(20.0)))
            .build();

        // Agora o accessLevel e userAccessLevel usam UByte
        temperatureNode.setAccessLevel(UByte.valueOf(3)); // Read/Write
        temperatureNode.setUserAccessLevel(UByte.valueOf(3));

        getNodeManager().addNode(temperatureNode);

        folderNode.addOrganizes(temperatureNode);
    }
}
