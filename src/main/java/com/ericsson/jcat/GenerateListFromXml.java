package com.ericsson.jcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ericsson.axe.jcat.JcatAxeTestCase;
import com.ericsson.jcat.ActionInfo;
import com.ericsson.jcat.AttributeInfo;
import com.ericsson.jcat.AttributeInfo.AttributeDataType;
import com.ericsson.jcat.ClassInfo;
import com.ericsson.jcat.ComRecordForXml;

/**
 * <p>
 * <b>Description:</b><br />
 * It reads the xml file from CPI, and generates a list of class ComRecordForXml which includes some key information
 * retrieved from xml file.
 * </p>
 * 
 * <p>
 * <b>Copyright:</b> Copyright (c) 2014
 * </p>
 * <p>
 * <b>Company:</b> Ericsson
 * </p>
 * 
 * @author ezhayix 2014-02-26 initial version
 * 
 */
public class GenerateListFromXml extends JcatAxeTestCase {
	static List<ComRecordForXml> comRecord = new ArrayList<ComRecordForXml>();
	static Element root;

	/**
	 * Find the element index of the list with specified parameters, if no matched element, return "-1"
	 * 
	 * @param classname
	 * @return index of the list
	 */
	static int getSpecificRecord(String mimname, String classname) {
		for (int i = 0; i < comRecord.size(); i++) {
			if (comRecord.get(i).Item.equals(classname) & comRecord.get(i).mimName.equals(mimname)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Display the generated list's content in order to check whether we got the correct information from the xml file.
	 */
	private static void displayListContent() {
		for (int i = 0; i < comRecord.size(); i++) {
			System.out.println("Class " + i + " is " + comRecord.get(i).Item);
			System.out.println("  mim name is " + comRecord.get(i).mimName);
			System.out.println("  parent name is " + comRecord.get(i).parent + " of " + comRecord.get(i).parMimName);
			System.out.println("  min is " + comRecord.get(i).cardnalityMin + " and max is "
					+ comRecord.get(i).cardnalityMax);
			for (int j1 = 0; j1 < comRecord.get(i).attributes.size(); j1++) {
				System.out.println("----Attributes " + j1 + " name is "
						+ comRecord.get(i).attributes.get(j1).getAttributeName());
				System.out.println("----Attributes " + j1 + " mandatory is "
						+ comRecord.get(i).attributes.get(j1).getMandatory());
				System.out.println("----Attributes " + j1 + " restricted is "
						+ comRecord.get(i).attributes.get(j1).getRestricted());
				System.out.println("----Attributes " + j1 + " KEY is " + comRecord.get(i).attributes.get(j1).getKey());
				System.out.println("----Attributes " + j1 + " NOTIFICATION is "
						+ comRecord.get(i).attributes.get(j1).getNotification());
				System.out.println("----Attributes " + j1 + " PERSISTENT is "
						+ comRecord.get(i).attributes.get(j1).getPersistent());
				System.out.println("----Attributes " + j1 + " READONLY is "
						+ comRecord.get(i).attributes.get(j1).getReadonly());
				System.out.println("----Attributes " + j1 + " data type is "
						+ comRecord.get(i).attributes.get(j1).getDataType().toString());
			}
			for (int j2 = 0; j2 < comRecord.get(i).childClass.size(); j2++) {
				System.out.println("----childClass " + j2 + " is " + comRecord.get(i).childClass.get(j2).getClassName()
						+ " of " + comRecord.get(i).childClass.get(j2).getMimName());
			}
			for (int j5 = 0; j5 < comRecord.get(i).absolutePath.size(); j5++) {
				System.out.println("----absolutePath " + j5 + " is "
						+ comRecord.get(i).absolutePath.get(j5).getClassName() + " of "
						+ comRecord.get(i).absolutePath.get(j5).getMimName());
			}
			for (int j3 = 0; j3 < comRecord.get(i).actions.size(); j3++) {
				System.out.println("----actions " + j3 + " is " + comRecord.get(i).actions.get(j3).getActionName());
				for (int j4 = 0; j4 < comRecord.get(i).actions.get(j3).getActionParameters().size(); j4++) {
					System.out.println("----actions par" + j4 + " is "
							+ comRecord.get(i).actions.get(j3).getActionParameters().get(j4));
				}
			}
		}
	}

	/**
	 * Add absolute path in the list which has the parent of each class.
	 */
	private static void addAbsolutePath() {
		for (int i = 0; i < comRecord.size(); i++) {
			int parIndex;
			while (!comRecord.get(i).absolutePath.get(0).getClassName().equals("ManagedElement")) {
				parIndex = getSpecificRecord(comRecord.get(i).absolutePath.get(0).getMimName(),
						comRecord.get(i).absolutePath.get(0).getClassName());
				for (int i1 = 0; i1 < comRecord.get(parIndex).absolutePath.size() - 1; i1++) {
					comRecord.get(i).absolutePath.add(i1, comRecord.get(parIndex).absolutePath.get(i1));
				}
			}
		}
	}

	/**
	 * Get relationship between mims to set the parent and child in the com classes.
	 */
	private static void getRelationshipInterMim() {
		for (Iterator<Element> i = root.elementIterator("interMim"); i.hasNext();) {
			Element interMim = i.next();
			if (interMim.hasContent()) {
				String parClass = interMim.element("relationship").element("containment").element("parent")
						.element("hasClass").attributeValue("name");
				String childClass = interMim.element("relationship").element("containment").element("child")
						.element("hasClass").attributeValue("name");
				String parMimName = interMim.element("relationship").element("containment").element("parent")
						.element("hasClass").element("mimName").getText();
				String childMimName = interMim.element("relationship").element("containment").element("child")
						.element("hasClass").element("mimName").getText();
				int min = Integer.parseInt(interMim.element("relationship").element("containment").element("child")
						.element("cardinality").elementText("min"));
				int max = Integer.parseInt(interMim.element("relationship").element("containment").element("child")
						.element("cardinality").elementText("max"));
				int childClassIndex = getSpecificRecord(childMimName, childClass);
				if (childClassIndex >= 0) {
					comRecord.get(childClassIndex).addParent(parClass);
					comRecord.get(childClassIndex).addParMim(parMimName);
					comRecord.get(childClassIndex).absolutePath.add(0, new ClassInfo(parClass, parMimName));
					comRecord.get(childClassIndex).addMin(min);
					comRecord.get(childClassIndex).addMax(max);
				}
				int parClassIndex = getSpecificRecord(parMimName, parClass);
				if (parClassIndex >= 0) {
					comRecord.get(parClassIndex).addChild(new ClassInfo(childClass, childMimName));
				}
			}
		}
	}

	/**
	 * 
	 * @param args
	 * @throws DocumentException
	 */
	public static void main(String[] args) throws DocumentException {
		System.out.println("Get the key information from xml file!");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File("c://2//MOMAXE_CPI_mp.xml"));
		root = document.getRootElement();
		int mimcount = 0;
		int classcount = 0;
		for (Iterator<Element> i = root.elementIterator("mim"); i.hasNext();) {
			Element mim = i.next();
			for (Iterator<Element> k = mim.elementIterator("class"); k.hasNext();) {
				Element cl = k.next();
				comRecord.add(new ComRecordForXml(cl.attributeValue("name"), "class", "", mim.attributeValue("name")));
				int actionscount = 0;
				for (Iterator<Element> l = cl.elementIterator("action"); l.hasNext();) {
					Element action = l.next();
					String name = action.attributeValue("name");
					List<String> pars = new ArrayList<String>();
					for (Iterator par = cl.elementIterator("action"); par.hasNext();) {
						Element parameter = (Element) par.next();
						pars.add(parameter.attributeValue("name"));
					}
					comRecord.get(classcount).actions.add(new ActionInfo(name, pars));
					actionscount++;
				}
				int attributescount = 0;
				List<AttributeInfo> attribute = new ArrayList<AttributeInfo>();
				// handle with attributes
				for (Iterator<Element> l = cl.elementIterator("attribute"); l.hasNext();) {
					Element at = l.next();
					String name = at.attributeValue("name");
					boolean isMandatory = at.elementIterator("mandatory").hasNext();
					boolean isRestricted = at.elementIterator("restricted").hasNext();
					boolean readonly = at.elementIterator("readOnly").hasNext();
					boolean notification = !at.elementIterator("noNotification").hasNext();
					boolean persistent = !at.elementIterator("nonPersistent").hasNext();
					boolean key = at.elementIterator("key").hasNext();
					AttributeDataType dataType;
					if (at.element("dataType").elementIterator("string").hasNext()) {
						dataType = AttributeDataType.isString;
					} else {
						if (at.element("dataType").elementIterator("derivedDataTypeRef").hasNext()) {
							dataType = AttributeDataType.isDerivedDataType;
						} else {
							dataType = AttributeDataType.isStruct;
						}
					}
					attribute.add(new AttributeInfo(name, isMandatory, isRestricted, readonly, notification,
							persistent, key, dataType));
					attributescount++;
				}
				comRecord.get(classcount).addAttributes(attribute);
				classcount++;
			}
			int relationcount = 0;
			for (Iterator<Element> irelation = mim.elementIterator("relationship"); irelation.hasNext();) {
				Element relation = irelation.next();
				if (relation.elementIterator("containment").hasNext()) {
					String parClass = relation.element("containment").element("parent").element("hasClass")
							.attributeValue("name");
					String childClass = relation.element("containment").element("child").element("hasClass")
							.attributeValue("name");
					int min = Integer.parseInt(relation.element("containment").element("child").element("cardinality")
							.elementText("min"));
					int max;
					if (relation.element("containment").element("child").element("cardinality").elementIterator("max")
							.hasNext()) {
						max = Integer.parseInt(relation.element("containment").element("child").element("cardinality")
								.elementText("max"));
					} else {
						max = -1;
					}
					int childClassIndex = getSpecificRecord(mim.attributeValue("name"), childClass);
					if (childClassIndex >= 0) {
						comRecord.get(childClassIndex).addParent(parClass);
						comRecord.get(childClassIndex).addParMim(mim.attributeValue("name"));
						comRecord.get(childClassIndex).absolutePath.add(0,
								new ClassInfo(parClass, mim.attributeValue("name")));
						comRecord.get(childClassIndex).addMin(min);
						comRecord.get(childClassIndex).addMax(max);
					}
					int parClassIndex = getSpecificRecord(mim.attributeValue("name"), parClass);
					if (parClassIndex >= 0) {
						comRecord.get(parClassIndex).addChild(new ClassInfo(childClass, mim.attributeValue("name")));
					}
				}
				relationcount++;
			}
			mimcount++;
		}
		getRelationshipInterMim();
		addAbsolutePath();
		displayListContent();
	}
}
