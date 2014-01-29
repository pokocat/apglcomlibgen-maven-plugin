package com.ericsson.jcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class FetchAndParseLogForXml {

	public static void main(String[] args) throws DocumentException {
		List<ComRecordForXml> comRecord = new ArrayList<ComRecordForXml>();
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File("c://2//MOMAXE_CPI_DWAXE_mp.xml"));
		Element root = document.getRootElement();
		System.out.println("root element name is " + root.element("mim").getName());
		int mimcount = 0;
		int classcount = 0;
		for (Iterator i = root.elementIterator("mim"); i.hasNext();) {
			Element mim = (Element) i.next();
			for (Iterator k = mim.elementIterator("class"); k.hasNext();) {
				Element cl = (Element) k.next();
				System.out.println(classcount + " class is " + cl.attributeValue("name"));
				comRecord.add(new ComRecordForXml(cl.attributeValue("name"), "class", ""));
				int attributescount = 0;
				List<String> attribute = new ArrayList<String>();
				for (Iterator l = cl.elementIterator("attribute"); l.hasNext();) {
					Element at = (Element) l.next();
					System.out.println("   " + attributescount + " attribute is " + at.attributeValue("name"));
					attribute.add(at.attributeValue("name"));
					comRecord.get(classcount).addAttributes(attribute);
					attributescount++;
				}
				classcount++;
			}
			int relationcount = 0;
			for (Iterator irelation = mim.elementIterator("relationship"); irelation.hasNext();) {
				Element relation = (Element) irelation.next();
				System.out.println(relationcount + " relationship is " + relation.attributeValue("name"));
				relationcount++;
			}
			mimcount++;
		}

		for (int i = 0; i < comRecord.size(); i++) {
			System.out.println("Class " + i + " is " + comRecord.get(i).Item);
			for (int j1 = 0; j1 < comRecord.get(i).attributes.size(); j1++) {
				System.out.println("----Attributes " + j1 + " is " + comRecord.get(i).attributes.get(j1));
			}
		}
	}
}
