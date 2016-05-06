package ch.inftec.ju.util.jms;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.util.jmx.MBeanUtils;

public class MBeanUtilsIT extends ContainerTest {
	/**
	 * Tests if JBoss MBeans are available through the PlatformMBeanServer.
	 * @throws Exception
	 */
	@Test
	public void canAccess_JBossMBean() throws Exception {
		String implTitle = MBeanUtils.queryPlatformMBeanServer("jboss.ws:service=ServerConfig").getAttribute("ImplementationTitle").get(String.class);
		Assert.assertEquals("JBossWS 5.1.3.Final (Apache CXF 3.1.4)", implTitle);
	}
}
