package com.ericsson.jcat;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * 
 * @author eduowan
 * 
 */
public abstract class AbstractPluginMojo extends AbstractMojo {
	/**
	 * The output directory to put automatically generated source files to
	 * 
	 * @parameter default-value="src/main/java"
	 * @required
	 */
	protected String commonLibClassesSourceFolder;

	/**
	 * Package Common Lib Classes
	 * 
	 * @parameter default-value="com.ericsson.apgl.common."
	 * @required
	 */
	protected String commonLibPackage;

	/**
	 * The origin log file of APG Linux.
	 * 
	 * @parameter default-value="src\test\resources\putty219_o.log"
	 * @required
	 */
	protected String apgLinuxLogFile;
	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Get variable project
	 * 
	 * @return
	 */
	protected MavenProject getProject() {
		return project;
	}
}
