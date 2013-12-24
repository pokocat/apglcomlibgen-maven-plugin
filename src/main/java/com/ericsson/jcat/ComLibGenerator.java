package com.ericsson.jcat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * @goal generate
 * @phase install
 * 
 * @author eduowan
 * 
 */
public class ComLibGenerator extends AbstractPluginMojo {
	/**
	 * Path separator used to construct class path {@value}
	 */
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	/**
	 * Source folder to put automatically generated source to
	 * 
	 * @parameter default-value="src/main/java"
	 * @required
	 */
	private String commonLibClassesSourceFolder;

	/**
	 * Package Common Lib Classes
	 * 
	 * @parameter default-value="com.ericsson.apgl.common."
	 * @required
	 */
	private String commonLibPackage;

	private Logger mLogger = Logger.getLogger(this.getClass());

	private File mDestDir;

	private void genClasses(List<ComRecord> classList) throws JClassAlreadyExistsException, IOException,
			ClassNotFoundException {

		JCodeModel codeModel;
		mLogger.info(classList.size() + " classes will be generated in total.");
		for (ComRecord classElement : classList) {

			codeModel = new JCodeModel();
			String className = classElement.className;
			mLogger.debug("Gonna generate:::" + className);

			JDefinedClass definedClass = codeModel._class(commonLibPackage + className);
			definedClass._extends(ComLib.class);

			// addCommonClassHeader("", definedClass.name(), definedClass.javadoc());

			JFieldVar mPath = definedClass.field(JMod.PUBLIC, codeModel.parseType("String"), "mPath",
					JExpr.lit(classElement.path));

			addAttributes(definedClass, classElement.attributes, codeModel);

			// constructor
			JMethod classConstructor = definedClass.constructor(JMod.PUBLIC);
			classConstructor.param(codeModel.ref(com.ericsson.axe.jcat.interfaces.IAPsession.class), "apCliss");
			classConstructor.param(codeModel.ref(String.class), "objectname");
			JBlock classConstructorBody = classConstructor.body();

			classConstructorBody.invoke("super").arg(JExpr.ref("apCliss"));
			JClass ComLib = codeModel.ref("com.ericsson.jcat.ComLib");
			JExpression execStr = JExpr.invoke("getObjectPath").arg(mPath.invoke("split").arg(JExpr.lit(",")));

			mPath.assign(classConstructorBody.invoke("setPath").arg(mPath));
			mPath.assign(classConstructorBody.invoke(mPath, "replace").arg(className + "=1")
					.arg(JExpr.lit(className + "=").plus(JExpr.ref("objectname"))));

			classConstructorBody.invoke(JExpr.ref("mApCliss"), "exec").arg(execStr);

			JVar printout = classConstructorBody.decl(codeModel.parseType("String"), "printout", JExpr.ref("mApCliss")
					.invoke("exec").arg("show"));

			for (String attrElement : classElement.attributes) {
				// TODO to judge the variable of ArrayList<String> more efficiently in log parsing phase
				if (attrElement.contains("ArrayList")) {
					continue;
				}
				attrElement = attrElement.split("=")[0];
				JConditional ifCondition = classConstructorBody._if(ComLib.staticInvoke("find").arg(printout)
						.arg(attrElement));
				JBlock ifBody = ifCondition._then();
				ifBody.assign(JExpr._this().ref(attrElement),
						ComLib.staticInvoke("findAndReturnFirstGroup").arg(printout).arg(attrElement + "=(.*)\n"));
			}
			printout.assign(JExpr.ref("mApCliss").invoke("exec").arg(""));

			classConstructor._throws(codeModel.directClass("com.ericsson.axe.jcat.exceptions.ap.APsessionException"))
					._throws(codeModel.directClass("InterruptedException"));

			// second constructor
			JMethod classConstructor2 = definedClass.constructor(JMod.PUBLIC);
			classConstructor2.param(codeModel.ref(com.ericsson.axe.jcat.interfaces.IAPsession.class), "apCliss");
			classConstructor2.body().invoke("this").arg(JExpr.ref("apCliss")).arg("1");
			classConstructor2._throws(codeModel.directClass("com.ericsson.axe.jcat.exceptions.ap.APsessionException"))
					._throws(codeModel.directClass("InterruptedException"));
			// end of Constructor

			// execWithoutCommit method
			JMethod execWithoutCommit = definedClass.method(JMod.PUBLIC, codeModel.VOID, "execWithoutCommit");
			execWithoutCommit._throws(codeModel.directClass("com.ericsson.axe.jcat.exceptions.ap.APsessionException"))
					._throws(codeModel.directClass("InterruptedException"));
			execWithoutCommit.param(codeModel.ref(String.class), "command");
			execWithoutCommit.body().invoke(JExpr.ref("mApCliss"), "exec").arg(JExpr.ref("command"));
			// execWithCommit method
			JMethod execWithCommit = definedClass.method(JMod.PUBLIC, codeModel.VOID, "execWithCommit");
			execWithCommit._throws(codeModel.directClass("com.ericsson.axe.jcat.exceptions.ap.APsessionException"))
					._throws(codeModel.directClass("InterruptedException"));
			execWithCommit.param(codeModel.ref(String.class), "command");
			execWithCommit.body().invoke(JExpr.ref("mApCliss"), "exec").arg(JExpr.ref("command"));
			execWithCommit.body().invoke(JExpr.ref("mApCliss"), "exec").arg("commit");
			// build Class
			codeModel.build(mDestDir);
		}
	}

	/**
	 * 
	 * @param jdc
	 * @param attributes
	 * @param codeModel
	 */
	private void addAttributes(JDefinedClass jdc, List<String> attributes, JCodeModel codeModel) {
		JClass arrayList = codeModel.ref(java.util.ArrayList.class);
		for (String element : attributes) {

			try {
				// TODO to judge the variable of ArrayList<String> more efficiently in log parsing phase
				if (element.contains("ArrayList")) {
					jdc.field(JMod.PUBLIC, arrayList, element);
				} else {
					jdc.field(JMod.PUBLIC, codeModel.parseType("String"), element);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param headerContent
	 * @param className
	 * @param jDocComment
	 */
	private void addCommonClassHeader(String headerContent, String className, JDocComment jDocComment) {
		jDocComment.add("<p>\n");
		jDocComment.add("<b>DO NOT EDIT! AUTOMATICALLY GENERATED "
				+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "</b>\n");
		jDocComment.add("</p>\n");

		jDocComment.add("<p>\n");
		jDocComment.add("<b>Description:</b><br />\n");
		jDocComment.add(className + " grouping class\n");
		jDocComment.add("</p>\n");

		jDocComment.add(headerContent);

		jDocComment.add("\n");
		jDocComment.add("<p>copyright Copyright (c) <\\p>" + new SimpleDateFormat("yyyy").format(new Date()) + "\n");
		jDocComment.add("<p>company Ericsson<\\p>\n");
		jDocComment.add("\n");
		jDocComment.add("@author ApgLComLibGenerator - " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
				+ " - Automatically generated\n");
	}

	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		mDestDir = new File(commonLibClassesSourceFolder);
		if (!mDestDir.canWrite()) {
			throw new IllegalStateException("Source folder: " + mDestDir + " can't be read!");
		} else if (!mDestDir.exists()) {
			try {
				if (!mDestDir.mkdirs()) {
					throw new IllegalStateException("Source folder " + mDestDir + " can't be created, please check!");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		mLogger.info("Common Library generator Mojo start.");
		List<ComRecord> classList = new FetchAndParseLog().fetchAndParseLog();

		try {
			genClasses(classList);
		} catch (JClassAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws MojoExecutionException, MojoFailureException {
		ComLibGenerator clg = new ComLibGenerator();

		clg.execute();
	}
}
