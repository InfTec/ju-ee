package ch.inftec.ee.jms;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.util.jmx.MBeanUtils;

public class JmsIT extends ContainerTest {
	@Test
	public void canSendMessage() {
		JmsTester.waitAndClear();
		
		JmsSenderBean sender = this.serviceLocator.cdi(JmsSenderBean.class);
		sender.send("canSendMessage");
		
		this.txHandler.commit();
		
		List<String> messages = JmsTester.waitAndGet(1);
		Assert.assertEquals("canSendMessage", messages.get(0));
	}
	
	@Test
	@DataVerify
	public void canReceive_message_usingVerifier() {
		JmsTester.waitAndClear();
		
		JmsSenderBean sender = this.serviceLocator.cdi(JmsSenderBean.class);
		sender.send("canReceive_message_usingVerifier");
		
		MBeanUtils.queryPlatformMBeanServer("");
	}
	public static final class CanReceive_message_usingVerifier extends DataVerifier {
		@Override
		public void verify() throws Exception {
			List<String> messages = JmsTester.waitAndGet(1);
			
			Assert.assertEquals(1, messages.size());
			Assert.assertEquals("canReceive_message_usingVerifier", messages.get(0));
		}
	}
}
