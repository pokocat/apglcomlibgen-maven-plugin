package com.ericsson.jcat;

import java.util.ArrayList;
import java.util.List;

public class ComRecordForXml {
	public String Item;
	public String type;
	// int line;
	// int blank;
	public String path;
	String className;
	public String parent;
	public List<String> attributes = new ArrayList<String>();
	List<String> subclass = new ArrayList<String>();

	public ComRecordForXml(String a, String b, String e) {
		Item = a;
		type = b;
		path = e;
	}

	public void addAttributes(List<String> attr) {
		attributes = attr;
	}

	public void addParent(String par) {
		parent = par;
	}
}
