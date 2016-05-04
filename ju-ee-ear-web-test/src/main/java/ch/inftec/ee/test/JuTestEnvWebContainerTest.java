package ch.inftec.ee.test;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.inftec.ju.ee.test.WebContainerTest;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.util.JuUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@JuTestEnv(systemProperties = {"classProp1=classPropVal1", "classProp2=classPropVal2"})
public class JuTestEnvWebContainerTest extends WebContainerTest {
	@Test
	public void systemProperties_canBeSet_onClass() {
		Assert.assertEquals("classPropVal1", JuUtils.getJuPropertyChain().get("classProp1"));
	}
	
	@Test
	@JuTestEnv(systemProperties = {"methodProp=methodPropVal"})
	public void _01_systemProperty_canBeSet_onMethod() {
		Assert.assertEquals("methodPropVal", JuUtils.getJuPropertyChain().get("methodProp"));
	}
	public static class SystemProperty_canBeSet_onMethod extends DataVerifier {
		@Override
		public void verify() throws Exception {
			Assert.assertEquals("methodPropVal", JuUtils.getJuPropertyChain().get("methodProp"));
		}
	}
	
	@Test
	@DataVerify
	public void _02_systemProperty_isReset() {
		Assert.assertFalse("methodPropVal".equals(JuUtils.getJuPropertyChain().get("methodProp")));
	}
	public static class SystemProperty_isReset extends DataVerifier {
		@Override
		public void verify() throws Exception {
			Assert.assertFalse("methodPropVal".equals(System.getProperty("methodProp")));
		}
	}
	
	@Test
	@JuTestEnv(systemProperties = {"classProp2=methodPropVal2"})
	public void systemProperties_canBeOverwritten_onMethod() {
		Assert.assertEquals("classPropVal1", JuUtils.getJuPropertyChain().get("classProp1"));
		Assert.assertEquals("methodPropVal2", JuUtils.getJuPropertyChain().get("classProp2"));
	}
	
	@Test
	@JuTestEnv(systemProperties = {"methodProp="})
	public void systemProperties_canBeEmpty() {
		Assert.assertEquals("", JuUtils.getJuPropertyChain().get("methodProp"));
	}
}