package com.ericsson.jcat;

import static org.testng.AssertJUnit.assertTrue;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.axe.jcat.exceptions.ap.APsessionException;
import com.ericsson.axe.jcat.interfaces.IAPsession;

/**
 * <p>
 * <b>Description:</b><br />
 * Template including some command methods.
 * </p>
 * 
 * <p>
 * <b>Copyright:</b> Copyright (c) 2013
 * </p>
 * <p>
 * <b>Company:</b> Ericsson
 * </p>
 * 
 * @author ezhayix 2013-12-27 initial version
 * 
 */
public class ComLib {
	protected IAPsession mApCliss;
	protected String mPath;
	protected String mPrintoutOfShow;

	public ComLib(IAPsession apCliss) {
		mApCliss = apCliss;
	}

	protected String getPath() throws APsessionException, InterruptedException {
		mPath = mApCliss.exec("prompt $dn");
		mApCliss.exec("prompt #~cliss");
		return mPath;
	}

	protected String setPath(String pathInClass) {
		String[] pathArray = pathInClass.split(",");
		if (!pathArray[0].contains("=")) {
			pathArray[0] += "=" + mApCliss.getManagedElement();
		}
		pathInClass = pathArray[0];
		for (int i = 1; i < pathArray.length; i++) {
			if (!pathArray[i].contains("=")) {
				pathInClass += "," + pathArray[i] + "=1";
			}
		}
		return pathInClass;
	}

	protected String show() throws APsessionException, InterruptedException {
		mPrintoutOfShow = mApCliss.exec("show");
		return mPrintoutOfShow;
	}

	protected String getObjectPath(String[] path) throws APsessionException, InterruptedException {
		String printout;
		String arguments;
		String command = "show";
		Pattern pattern;
		Matcher matcher;
		for (int i = 0; i < path.length; i++) {
			if (path[i].contains("=")) {
				arguments = path[i];
			} else {
				printout = mApCliss.exec(command);
				pattern = Pattern.compile(path[i] + "=.*($|\n|\\s\n)");
				matcher = pattern.matcher(printout);
				assertTrue("the parameter array's content has some errors!", matcher.groupCount() == 1);
				arguments = matcher.group().replace("\n", "").trim();
			}
			if (i < 1) {
				command = command + " " + arguments;
			} else {
				command = command + "," + arguments;
			}
		}
		return command.replace("show ", "");
	}

	/**
	 * @param s
	 * @param regExp
	 * @return boolean
	 *         <p\>
	 *         Summary Evaluates whether String matches a regular expression, works for Strings containing \n (newline
	 *         character)
	 *         <p/>
	 *         <p\>
	 *         Example description: Find out if the printout contains the pattern.
	 *         <p/>
	 * 
	 *         String printout = "EXCHANGE IDENTITY DATA\n\nIDENTITY\n303E1L120_9BLSV13.0_B18\n\nEND" ; String pattern =
	 *         "303E1L120_9BLSV13.0_B18";
	 * 
	 *         if( StringUtil.find( printout, pattern ) ) { info("The printout contains the pattern"); } else {
	 *         info("The printout do NOT contain the pattern"); }
	 * 
	 */
	static public boolean find(String s, String regExp) {
		return Pattern.compile(regExp, Pattern.DOTALL).matcher(s).find();
	}

	/**
	 * Finds and extracts the first regular expression group from s. A group is defined with ().
	 * <p/>
	 * <code>
	 *  String subexpression = StringUtil.findAndReturnFirstGroup("BC3","BC([0-9]+)");
	 * 
	 *  (returns "3")
	 *  </code>
	 * 
	 * @param s
	 * @param regExp
	 * @return group one
	 */
	static public String findAndReturnFirstGroup(String s, String regExp) {
		Pattern pattern = Pattern.compile(regExp, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	protected void setRecordContent(IAPsession apSession, ArrayList<String> recordName, String cmdStr)
			throws APsessionException, InterruptedException {
		String printout = apSession.exec("show " + cmdStr);
		String[] pr = printout.split("\n");
		for (int i = 1; i < pr.length; i++) {
			recordName.add(pr[i].trim());
		}
	}
}
