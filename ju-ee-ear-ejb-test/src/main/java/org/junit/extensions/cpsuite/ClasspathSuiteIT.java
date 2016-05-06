package org.junit.extensions.cpsuite;

import org.junit.Assert;
import org.junit.Test;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.extensions.cpsuite.ClasspathSuiteIT.ClasspathSuiteIT_TestVerifier;
import org.junit.extensions.cpsuite.ClasspathSuiteIT.ClasspathSuiteTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test case to verify that ClasspathSuite is working in an application server
 * (i.e. EAR) context.
 * 
 * @author martin.meyer@inftec.ch
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ClasspathSuiteTest.class, ClasspathSuiteIT_TestVerifier.class })
public class ClasspathSuiteIT {
	public static boolean ran = false;

	@RunWith(ClasspathSuite.class)
	@ClassnameFilters(".*ClasspathSuiteIT_Test")
	@IncludeJars(true)
	public static class ClasspathSuiteTest {
	}

	public static class ClasspathSuiteIT_Test {
		@Test
		public void isRun() {
			ClasspathSuiteIT.ran = true;
		}
	}

	public static class ClasspathSuiteIT_TestVerifier {
		@Test
		public void verifyRun() {
			Assert.assertTrue("ran flag was not set", ran);
		}
	}
}
