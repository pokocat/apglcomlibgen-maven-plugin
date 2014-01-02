package com.ericsson.jcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class NewTest {
	@Test
	public void f() {
		ComLibGenerator libGen = new ComLibGenerator();
		libGen.apgLinuxLogFile = "src\\test\\resources\\putty219.log";
		libGen.commonLibClassesSourceFolder = "/src/main/java";
		libGen.commonLibPackage = "com.ericsson.apgl.common.";
		try {
			libGen.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MojoFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@BeforeTest
	public void beforeTest() {
	}

	@AfterTest
	public void afterTest() {
	}

}
