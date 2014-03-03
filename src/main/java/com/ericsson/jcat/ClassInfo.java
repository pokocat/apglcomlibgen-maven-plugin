package com.ericsson.jcat;

public class ClassInfo {
	public String className;
	public String mimName;

	public ClassInfo(String a, String b) {
		className = a;
		mimName = b;
	}

	public String getClassName() {
		return className;
	}

	public String getMimName() {
		return mimName;
	}
}
