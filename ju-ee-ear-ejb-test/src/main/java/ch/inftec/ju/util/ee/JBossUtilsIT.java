package ch.inftec.ju.util.ee;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;

public class JBossUtilsIT extends ContainerTest {
	@Test
	public void jmxInfo_canQueryMessageCount() {
		Long messageCount = JBossUtils.queryJmx().jms().queueInfo("jms.queue.ju").getMessageCount();
		
		Assert.assertEquals(new Long(0L), messageCount);
	}
}
