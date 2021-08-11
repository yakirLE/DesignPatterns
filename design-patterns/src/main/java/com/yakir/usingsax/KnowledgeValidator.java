package com.yakir.usingsax;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.yakir.utils.Utils;


public class KnowledgeValidator extends DefaultHandler {
	
	private Map<String, Set<String>> missingSets = new HashMap<>();
	private Set<String> sets;
	
	private void parseXML(String xml) {
		parseXML(xml, this);
	}
	
	public void execute(String xmlFile) {
		sets = SetsMapper.getSets(xmlFile);
		parseXML(xmlFile);
		System.out.println(missingSets);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String name = attributes.getValue("name");
		String useTable = attributes.getValue("use_table");
		String preload = attributes.getValue("preload_sets");
		Set<String> sets = new HashSet<>();
		sets.addAll(Utils.stringToSet(useTable));
		sets.addAll(Utils.stringToSet(preload));
		if(!this.sets.containsAll(sets)) {
			Set<String> missingSets = new HashSet<>(sets);
			missingSets.removeAll(this.sets);
			this.missingSets.put(name, missingSets);
		}
	}
	
	public static void main(String[] args) {
		KnowledgeValidator x = new KnowledgeValidator();
		x.execute("C:\\Workspaces\\Binbeal-2\\assets\\mediator\\platform\\MEDIATOR\\static_content\\webapp\\WEB-INF\\classes\\conf\\mediator\\Host_Weblogic_Login_scanner_knowledge.xml");
	}
	
	public static <T extends DefaultHandler> void parseXML(String xml, T sax) {
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
			sp.parse(xml, sax);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class SetsMapper extends DefaultHandler {
		
		private Set<String> setsName = new HashSet<>();
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			String name = attributes.getValue("name");
			String synn = attributes.getValue("set_synonym");
			if(name != null && !name.isEmpty()) {
				setsName.add(name);
			}
			
			if(synn != null && !synn.isEmpty()) {
				setsName.add(synn);
			}
		}
		
		public static Set<String> getSets(String xmlFile) {
			SetsMapper x = new SetsMapper();
			x.parseXML(xmlFile);
			return x.setsName;
		}
		
		private void parseXML(String xml) {
			KnowledgeValidator.parseXML(xml, this);
		}
	}
}
