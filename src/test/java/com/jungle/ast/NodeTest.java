package com.jungle.ast;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class NodeTest {
    @Test
    public void testSaveAndLoad() throws IOException {
        INode inputNode = new Node(NodeType.SEQUENCE)
                .withLeft(new Node(NodeType.PRINT)
                        .withLeft(new Node(NodeType.LITERAL_STRING)
                                .withValue("Hello\tworld!\n")));
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
        Node.save(bufferedWriter, inputNode);

        bufferedWriter.flush();

        StringReader stringReader = new StringReader(stringWriter.toString());
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        INode outputNode = Node.load(bufferedReader);

        assertEquals(inputNode, outputNode);
    }
}
