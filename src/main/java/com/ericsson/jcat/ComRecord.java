package com.ericsson.jcat;

import java.util.ArrayList;
import java.util.List;

public class ComRecord {
	String Item;
	String type;
	int line;
	int blank;
	String path;
	String className;
	List<String> attributes = new ArrayList<String>();
	List<String> subclass = new ArrayList<String>();

	ComRecord(String a, String b, int c, int d, String e) {
		Item = a;
		type = b;
		line = c;
		blank = d;
		path = e;
	}
}
