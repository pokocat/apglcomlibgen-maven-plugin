package com.ericsson.jcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.reporters.ExitCodeListener;

public class FetchAndParseLog {

	/**
	 * File not found exit code = {@value}
	 */
	private static final int FILE_NOT_FOUND_EXIT_CODE = -1;
	/**
	 * File can't be read exit code = {@value}
	 */
	private static final int FILE_CANNOT_READ_EXIT_CODE = -2;
	
	private Logger logger = Logger.getLogger(this.getClass());

	public List<ComRecord> fetchAndParseLog(String apgLinuxLogFile) {
		logger.info("Fetching logs now...");
		File logFile = new File(apgLinuxLogFile);
		if (!logFile.exists()) {
			logger.error("Log file not found!!! Check at: " + apgLinuxLogFile);
			System.exit(FILE_NOT_FOUND_EXIT_CODE);
		} else if (!logFile.canRead()) {
			logger.error("Log file can't be read! Please check at: " + apgLinuxLogFile);
			System.exit(FILE_CANNOT_READ_EXIT_CODE);
		}
		logger.info("gonna fetch log from : " + apgLinuxLogFile);

		logger.info(logFile.getName() + " is recently modified at : "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logFile.lastModified()));

		List<ComRecord> mCom = new ArrayList<ComRecord>();

		mCom = readFileByLines(logFile);

		List<ComRecord> mComClass = addClassName(addAttributes(addAbsolutePath(addSubclass(filterRepeatedClass(mCom))),
				mCom));

		return mComClass;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public List<ComRecord> readFileByLines(File file) {
		List<ComRecord> showList = new ArrayList<ComRecord>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			String ItemString = null;
			String pathString = "";
			String ItemName = "";
			String attributes1;
			String attributes2;
			String attributes;
			int line = 1;

			while ((tempString = reader.readLine()) != null) {
				int spaceCount = 0;
				while (tempString.charAt(spaceCount) == ' ') {
					spaceCount++;
				}
				ItemString = tempString.substring(spaceCount).replace("[] <empty>", "null").replace(" <default>", "")
						.replace(" <read-only>", "");
				int end = ItemString.indexOf("=");
				if (end != -1 & ItemString.charAt(0) >= 'A' & ItemString.charAt(0) <= 'Z') {
					ItemName = ItemString.substring(0, end);
					showList.add(new ComRecord(ItemName, "CLASS", line, spaceCount, pathString));
				} else {
					if (end != -1 & ItemString.charAt(0) != '"') {
						attributes1 = ItemString.split("=")[0];
						attributes2 = ItemString.split("=")[1].replace("\"", "");
						if (!attributes2.equals("null")) {
							attributes = attributes1 + "=\"" + attributes2 + "\"";
						} else {
							attributes = attributes1 + "=" + attributes2;
						}
						showList.add(new ComRecord(attributes, "ATTRIBUTES", line, spaceCount, pathString));
					} else {
						if (ItemString.contains("(")) {
							showList.add(new ComRecord(ItemString, "METHOD", line, spaceCount, pathString));
						} else {
							if (ItemString.charAt(0) == '"') {
								showList.add(new ComRecord(ItemString, "CONTENT", line, spaceCount, pathString));
							} else {
								showList.add(new ComRecord(ItemString, "RECORD", line, spaceCount, pathString));
							}
						}
					}
				}
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return showList;
	}

	/**
	 * According to the list of ComRecord which including all lines of "show all verbose", generate the new list only
	 * including none-repeated classes.
	 * Rules:
	 * the first class is the first record of list;
	 * check the following record in the list, if the record doesn't have the same blank and Item with the record of
	 * comClass, add it into comClass.
	 * 
	 * @param list -- the list of ComRecord, including all lines of the result of "show all verbose".
	 * @return comClass -- the new list only including none-repeated classes.
	 */
	public static List<ComRecord> filterRepeatedClass(List<ComRecord> list) {
		List<ComRecord> comClass = new ArrayList<ComRecord>();
		for (int j = 0; j < list.size(); j++) {
			if (list.get(j).type.contains("CLASS")) {
				if (j == 0) {
					comClass.add(list.get(j));
				} else {
					boolean findSamePath = false;
					boolean findSameName = false;
					for (int i = 1; i <= comClass.size(); i++) {
						if (list.get(j).path.equals(comClass.get(comClass.size() - i).path)) {
							findSamePath = true;
							if (list.get(j).Item.equals(comClass.get(comClass.size() - i).Item)) {
								findSameName = true;
								break;
							} else {
								findSameName = false;
							}
						}
					}

					if (false == findSamePath || false == findSameName) {
						comClass.add(list.get(j));
					}
				}
			}
		}
		return comClass;
	}

	/**
	 * Add the absolute path in the list of all none-repeated classes.
	 * 
	 * @param list -- the list of all none-repeated classes with path="".
	 * @return list -- the list of all none-repeated classes with the absolute path.
	 */
	public static List<ComRecord> addAbsolutePath(List<ComRecord> list) {
		String pathString = "";
		for (int i = 0; i < list.size(); i++) {
			// set absolute path
			if (i > 0) {
				ComRecord previousCom = list.get(i - 1);
				if (previousCom.blank == list.get(i).blank) {
					pathString = previousCom.path;

				} else {
					if (previousCom.blank == (list.get(i).blank - 3)) {
						pathString = previousCom.path + "," + previousCom.Item;

					} else {
						for (int j = 2; j <= i; j++) {
							if (list.get(i - j).blank == list.get(i).blank) {
								pathString = list.get(i - j).path;
								// list.get(i).path = pathString;
								// System.out.println("4-line:" + i + " " + pathString);
								break;
							}
						}

					}
				}
			}
			list.get(i).path = pathString.replace(",ManagedElement", "ManagedElement");
		}
		list.get(0).path = "ManagedElement";
		for (int i = 1; i < list.size(); i++) {
			list.get(i).path += "," + list.get(i).Item;
		}
		return list;
	}

	/**
	 * Add attributes list to the class list.
	 * 
	 * @param list --- class list
	 * @param listAll ---list of all records from "show all"
	 * @return list --- class list with attributes
	 */
	public static List<ComRecord> addAttributes(List<ComRecord> list, List<ComRecord> listAll) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < listAll.size(); j++) {
				if (listAll.get(j).line == list.get(i).line + 1) {
					while (listAll.get(j).blank == list.get(i).blank + 3 && j < listAll.size() - 1) {
						if (listAll.get(j).type.equals("ATTRIBUTES")) {
							list.get(i).attributes.add(listAll.get(j).Item);
						} else {
							if (listAll.get(j).type.equals("RECORD")) {
								list.get(i).attributes.add(listAll.get(j).Item + " = new ArrayList<String>();");
							} else {
								if (listAll.get(j).type.equals("METHOD")) {
									list.get(i).attributes.add("Method_" + listAll.get(j).Item + " = "
											+ listAll.get(j).Item + ";");
								}
							}
						}
						j++;
					}
					break;
				}
			}
		}
		return list;
	}

	/**
	 * Add subclasses into the list of class.
	 * 
	 * @param classList --- class list
	 * @return classList --- class list with subclasses
	 */
	public static List<ComRecord> addSubclass(List<ComRecord> classList) {
		int endOfSameLevel = 0;
		for (int i = 0; i < classList.size() - 1; i++) {
			for (int j = i + 1; j < classList.size(); j++) {
				if (classList.get(j).blank == classList.get(i).blank) {
					endOfSameLevel = j;
					break;
				} else {
					if (0 == i) {
						endOfSameLevel = classList.size();
					} else {
						endOfSameLevel = i;
					}
				}
			}
			for (int j = i + 1; j < endOfSameLevel; j++) {
				if (classList.get(j).blank == classList.get(i).blank + 3) {
					classList.get(i).subclass.add(classList.get(j).Item);
				}
			}
		}
		return classList;
	}

	/**
	 * Add class name to avoid the same names of java files.
	 * 
	 * @param classList
	 * @return
	 */
	public static List<ComRecord> addClassName(List<ComRecord> classList) {
		boolean findSameClass;
		for (int i = 0; i < classList.size(); i++) {
			findSameClass = false;
			for (int j = 0; j < classList.size() - 1; j++) {
				if (classList.get(i).Item.equals(classList.get(j + 1).Item) && i != (j + 1)) {
					findSameClass = true;
					break;
				}
			}
			if (findSameClass) {
				classList.get(i).className = classList.get(i).Item + "-"
						+ classList.get(i).path.split(",")[classList.get(i).path.split(",").length - 2];
			} else {
				classList.get(i).className = classList.get(i).Item;
			}
		}
		return classList;
	}

}
