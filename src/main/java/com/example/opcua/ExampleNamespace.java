package src.main.java.com.example.opcua;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class ExampleNamespace extends ManagedNamespace {

    private final OpcUaServer server;

    public ExampleNamespace(OpcUaServer server) {
        super(server, "urn:example:namespace");
        this.server = server;
        createNodes();
    }

    private void createNodes() {
        UaFolderNode folderNode = new UaFolderNode(
            server.getNodeMap(),
            new NodeId(getNamespaceIndex(), "MyObjects"),
            new LocalizedText("MyObjects")
        );

        server.getNodeMap().addNode(folderNode);
        server.getUaNamespace().addReference(
            Identifiers.ObjectsFolder,
            Identifiers.Organizes,
            true,
            folderNode.getNodeId().expanded(),
            Identifiers.Organizes,
            false
        );

        UaVariableNode myVariable = UaVariableNode.builder(server.getNodeMap())
            .setNodeId(new NodeId(getNamespaceIndex(), "MyVariable"))
            .setBrowseName("MyVariable")
            .setDisplayName(LocalizedText.english("MyVariable"))
            .setDataType(Identifiers.Int32)
            .setTypeDefinition(Identifiers.BaseDataVariableType)
            .setValue(new DataValue(new Variant(42)))
            .build();

        myVariable.setAccessLevel((byte) 0b11); // read/write
        myVariable.setUserAccessLevel((byte) 0b11);

        server.getNodeMap().addNode(myVariable);
        folderNode.addOrganizes(myVariable);
    }
}
