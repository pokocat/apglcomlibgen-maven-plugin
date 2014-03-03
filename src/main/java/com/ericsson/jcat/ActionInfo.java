package com.ericsson.jcat;

import java.util.ArrayList;
import java.util.List;

public class ActionInfo {
	private final String mName;
	private List<String> mParameters = new ArrayList<String>();

	/**
	 * 
	 * @param name
	 * @param pars
	 */
	public ActionInfo(String name, List<String> pars) {
		mName = name;
		mParameters = pars;
	}

	/**
	 * 
	 * @return
	 */
	public String getActionName() {
		return mName;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getActionParameters() {
		return mParameters;
	}
}
