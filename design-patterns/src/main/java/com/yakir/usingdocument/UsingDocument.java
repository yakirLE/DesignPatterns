package com.yakir.usingdocument;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UsingDocument {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbFactory.newDocumentBuilder();
		Document doc = db.parse(new File("C:\\del\\OEM\\xml.xml"));
		Document outDoc = db.newDocument();
//		outDoc.appendChild(doc.createElement("ROOT"));
//		doSomething(doc.getDocumentElement());
		NodeList lst = doc.getDocumentElement().getChildNodes();
		for(int i = 0; i < lst.getLength(); i++) {
			Node currNode = lst.item(i);
			NamedNodeMap attrs = currNode.getAttributes();
			for(int j = 0; attrs != null && j < attrs.getLength(); j++) {
				Node att = attrs.item(j);
				System.out.println(att.getNodeName() + "\t" + att.getNodeValue());
			}
			
			System.out.println("---");
		}
	}

//	public static void doSomething(Node node) {
//	    // do something with the current node instead of System.out
//	    System.out.println(node.getNodeName());
//
//	    NodeList nodeList = node.getChildNodes();
//	    for (int i = 0; i < nodeList.getLength(); i++) {
//	        Node currentNode = nodeList.item(i);
//	        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
//	            //calls this method for all the children which is Element
//	            doSomething(currentNode);
//	        }
//	    }
//	}
}
