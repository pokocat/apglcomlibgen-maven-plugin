package com.ericsson.jcat;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ComLibGeneratorTest {
	private static Logger logger = Logger.getLogger(ComLibGeneratorTest.class);

	
	public void f() {
		logger.info("Testing start!");
		logger.info("Creating file instance for configuration pom");

	}

	@BeforeClass
	public void beforeTest() {

		logger.info("Before testing");
	}

	@AfterClass
	public void afterTest() {

	}

}
