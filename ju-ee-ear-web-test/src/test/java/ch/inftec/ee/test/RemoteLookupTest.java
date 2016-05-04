package ch.inftec.ee.test;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.client.JndiServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.ee.test.TestRemote;

public class RemoteLookupTest {
	@Test
	public void canLookup_testFacadeBean_usingServiceLocatorBuilder() throws Exception {
		JndiServiceLocator loc = ServiceLocatorBuilder.createRemoteByConfigurationFiles();
		
		TestRemote testRemote = loc.lookup(TestRemote.class);
		Assert.assertEquals("TestRemoteBean says hello to ServiceLocatorBuilder", testRemote.getGreeting("ServiceLocatorBuilder"));
	}
}
