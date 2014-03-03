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
	public String mimName;
	public String parMimName;
	public String parent;
	public int cardnalityMin = -1;
	public int cardnalityMax = -1;
	public List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();
	public List<ClassInfo> childClass = new ArrayList<ClassInfo>();
	public List<ClassInfo> absolutePath;
	public List<ActionInfo> actions = new ArrayList<ActionInfo>();

	public ComRecordForXml(String classname, String type, String path) {
		Item = classname;
		type = type;
		path = path;
	}

	public ComRecordForXml(String classname, String type, String path, String mimname) {
		Item = classname;
		type = type;
		path = path;
		mimName = mimname;
		ClassInfo defaultClassInfo = new ClassInfo(classname, mimname);
		absolutePath = new ArrayList<ClassInfo>();
		absolutePath.add(defaultClassInfo);
	}

	public void addAttributes(List<AttributeInfo> attr) {
		attributes = attr;
	}

	public void addParent(String par) {
		parent = par;
	}

	public void addChild(ClassInfo child) {
		childClass.add(child);
	}

	public void addParMim(String parmim) {
		parMimName = parmim;
	}

	public void addMin(int min) {
		cardnalityMin = min;
	}

	public void addMax(int max) {
		cardnalityMax = max;
	}

}
