package com.yakir.usingsax;

import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UsingSAX extends DefaultHandler {
	
	@SuppressWarnings("unused")
	private Stack<String> stack;
	
	public UsingSAX() {
		this.stack = new Stack<>();
	}
	
	public void execute(String xml) {
		parseXML(xml);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("connection") && 
		   "IsActiveActive_XCLI".equals(attributes.getValue("name"))) {
			String from = attributes.getValue("from");
			String to = attributes.getValue("to");
			if(!from.equals(to)) {
				System.out.println(from + "\t" + to);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
	
	private void parseXML(String xml) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		try {
			sp.parse(xml, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
