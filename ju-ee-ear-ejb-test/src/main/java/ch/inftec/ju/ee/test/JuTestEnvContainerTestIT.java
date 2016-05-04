package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.util.JuUtils;

@JuTestEnv(systemProperties = {"classProp1=classPropVal1", "classProp2=classPropVal2"})
public class JuTestEnvContainerTestIT extends ContainerTest {
	@Test
	public void systemProperties_canBeSet_onClass() {
		Assert.assertEquals("classPropVal1", JuUtils.getJuPropertyChain().get("classProp1"));
	}
	
	@Test
	@JuTestEnv(systemProperties = {"methodProp=methodPropVal"})
	public void systemProperty_canBeSet_onMethod() {
		Assert.assertEquals("methodPropVal", JuUtils.getJuPropertyChain().get("methodProp"));
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
