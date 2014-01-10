package com.ericsson.jcat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Dom4jBuildXmlDemo {

	private String getDesiredString(String regex, String sourceStr) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sourceStr);
		if (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}

	private String getAttribute(String sourceStr) {
		// TODO Investigate if <read-only> attribute is needed
		String tempStr = sourceStr.replace("<read-only>", "").replace("<default>", "").replace("<empty>", "").trim();
		if (tempStr.contains("=")) {
			return tempStr.split("=")[0];
		}
		/*
		 * *********** comment out for not assign default value
		 * String tempStr = sourceStr.replace("<read-only>", "").replace("<default>", "").replace("<empty>", "").trim();
		 * String[] attribute = new String[2];
		 * if (!tempStr.contains("=")) {
		 * attribute[0] = tempStr;
		 * attribute[1] = "";
		 * } else {
		 * for (int i = 0; i < attribute.length; i++) {
		 * attribute[i] = tempStr.split("=")[i].replace("\"", "").replace("[]", "");
		 * }
		 * }
		 * getAttribute(eachLine)
		 */
		return tempStr;
	}

	private int getLevel(String str, int index) {
		if (index < str.length()) {
			if (str.charAt(index) == ' ') {
				return getLevel(str, ++index);
			}
			return index / 3;
		}
		return 0;
	}

	public void fileToXml(String file) {

		BufferedReader reader = null;
		String regexForClassName = "^\\s*[A-Z]\\w+=[\\w]*";// For recognize the class name
		try {
			reader = new BufferedReader(new FileReader(file));
			Document document = DocumentHelper.createDocument();
			Element[] elements = new Element[20];
			String eachLine;
			int level = 0;
			int lineNum = 0;
			while ((eachLine = reader.readLine()) != null) {
				lineNum++;
				level = getLevel(eachLine, 0);
				System.out.println("line::: " + lineNum + " is ::::" + eachLine + " - LEVEL:::" + level);

				String classNode = getDesiredString(regexForClassName, eachLine);
				if (!classNode.isEmpty()) {
					if (level == 0) {
						elements[level] = document.addElement("ClassNode");
					} else {
						elements[level] = elements[level - 1].addElement("ClassNode");
					}
					/*
					 * comment out for not assign default value and type for classnode
					 * elements[level].addAttribute("type", "ClassNode");
					 * if (classNode.contains("=")) {
					 * elements[level].addText(classNode.trim().split("=")[1]);
					 * }
					 */
					elements[level].addText(classNode.trim().split("=")[0]);

				} else {
					if (lineNum == 0 && level == 0) {
						throw new Error("No root element for XML?");
					}
					String attributeStr = getAttribute(eachLine);
					if (attributeStr.startsWith("\"")) {
						// It's a string value for parameter not variable name! Skip add attribute
						continue;
					}
					elements[level] = elements[level - 1].addElement("Attribute");
					// Element attributeElement = elements[level].addElement("Attribute");

					elements[level].addText(attributeStr.replace("[]", ""));

					if (attributeStr.contains("[]")) {
						elements[level].addAttribute("type", "Array");
					} else {
						elements[level].addAttribute("type", "String");
					}

					// attributeElement.addText(attributeStr[1]);
					// attributeElement.addAttribute("type", "Attribute");
					// System.out.println("Added - Attribute: " + attributeStr[0] + " with value: " + attributeStr[1]);
				}
			}
			System.out.println(lineNum + " lines of log has been read.");
			System.out.println(document.asXML());
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setSuppressDeclaration(true);
			format.setIndent(true);
			format.setIndentSize(4);
			// format.setIndent(" ");
			format.setNewlines(true);

			// System.out.println(document.asXML());

			Writer fileWriter = new FileWriter("src/test/resources/demo.xml");
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			xmlWriter.write(document);

			xmlWriter.flush();
			xmlWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				// Ignore any exceptions when closing reader;
			}
		}

	}


	public static void main(String[] args) throws IOException {
		Dom4jBuildXmlDemo demo = new Dom4jBuildXmlDemo();
		demo.fileToXml("src/test/resources/putty219.log");
	}
}
