package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTestRunnerRule.TestRunnerType;
import ch.inftec.ju.util.JuRuntimeException;

public class ContainerTestRunnerRuleIT {
	@Rule
	public ContainerTestRunnerRule testRunnerRule = new ContainerTestRunnerRule(TestRunnerType.CONTAINER);
	
	@Test
	public void canRunTestCode_inContainer() {
//		for (Object key : System.getProperties().keySet()) {
//			System.out.println(key  + ": " + System.getProperty(key.toString()));
//		}
		
		Assert.assertTrue(System.getProperty("jboss.server.base.dir").length() > 1);
	}
	
	@Test(expected=RuntimeException.class)
	public void expectsException_works() {
		throw new RuntimeException("Expected exception");
	}
	
//	@Ignore("Error test")
	@Test
	public void displaysAssertionError() {
		Assert.fail("Error test");
	}
	
//	@Ignore("Error test")
	@Test
	public void displaysException() {
		throw new JuRuntimeException("Error");
	}
}
