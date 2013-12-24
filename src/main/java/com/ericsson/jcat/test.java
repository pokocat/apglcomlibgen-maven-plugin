package com.ericsson.jcat;

import java.io.File;
import java.io.IOException;

import com.ericsson.axe.jcat.interfaces.IAPsession;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JType;

public class test {

	static final String GENERATED_CLASS_DIR = "src\\main\\java";
	static final String GENERATED_CLASS_PACKAGE = "com.ericsson.axe.test";

	public static void main(String args[]) throws JClassAlreadyExistsException, IOException {
		genTest();

	}

	private static void genTest() {
		System.out.println("\"" + "=" + "\"");
	}
}
