package com.yakir.sandbox.bofa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SysPropsReader extends DefaultHandler {
	
	private Stack<String> path = new Stack<>();
	
	public void execute(String path) {
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
			sp.parse(path, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String getPath() {
		List<String> p = new ArrayList<>(path);
		return "/" + p.stream().collect(Collectors.joining("/"));
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		path.push(qName);
		if(isSysProp())
			startElementInternal(uri, localName, qName, attributes);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(isSysProp())
			endElementInternal(uri, localName, qName);
		path.pop();
	}
	
	protected boolean isSysProp() {
		return getPath().equals("/Configuration/SystemSetting/Property");
	}
	
	protected abstract void startElementInternal(String uri, String localName, String qName, Attributes attributes) throws SAXException;
	
	protected abstract void endElementInternal(String uri, String localName, String qName) throws SAXException;
	
	public static class SysPropDefaultValueGetter extends SysPropsReader {

		private static Map<String, Set<String>> nameToDefaultValue = new HashMap<>();
		
		@Override
		protected void startElementInternal(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			String propName = null;
			String defValue = null;
			for(int i = 0; i < attributes.getLength() && (propName == null || defValue == null); i++) {
				String name = attributes.getQName(i);
				String value = attributes.getValue(i);
				if(name.equals("name"))
					propName = value;
				else if(name.equals("defaultValue"))
					defValue = value;
			}
			
			nameToDefaultValue.computeIfAbsent(propName, k -> new HashSet<>()).add(defValue);
		}

		@Override
		protected void endElementInternal(String uri, String localName, String qName) throws SAXException {
		}
		
		public static Map<String, Set<String>> getMap() {
			return nameToDefaultValue;
		}
		
	}
	
	public static class UsedSysPropsPrinter extends SysPropsReader {
		
		private Map<String, String> nameToValue = new HashMap<>();
		
		@Override
		protected void startElementInternal(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			boolean print = false;
			StringJoiner sj = new StringJoiner(" ");
			String propName = null;
			String propValue = null;
			for(int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				String value = attributes.getValue(i);
				if(name.equals("defaultValue") ||
						name.equals("name") ||
						name.equals("privacy") ||
						name.equals("valuetype")) {
					sj.add(name + "=\"" + value + "\"");
					if(name.equals("name"))
						propName = value;
				}
				
				if(name.equals("value")) {
					print = true;
					sj.add(name + "=\"" + value + "\"");
					propValue = value;
				}
			}

			if(print) {
//				System.out.println(sj.toString());
				nameToValue.put(propName, propValue);
			}
		}

		@Override
		protected void endElementInternal(String uri, String localName, String qName) throws SAXException {
		}

		public Map<String, String> getNameToValue() {
			return nameToValue;
		}
	}
}