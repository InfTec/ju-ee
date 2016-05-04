package ch.inftec.ee.bean;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.client.JndiServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.util.AssertUtil;

/**
 * Contains tests that are performed on a Remote bean without using the ContainerTest facility.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class ContainerTestContextRemoteIT {
	private Logger logger = LoggerFactory.getLogger(ContainerTestContextRemoteIT.class);

	private RemoteTestsRemote getRemoteTestsRemote() {
		JndiServiceLocator serviceLocator = ServiceLocatorBuilder.createRemoteByConfigurationFiles();

		return serviceLocator.lookup(RemoteTestsRemote.class);
	}

	@Test
	public void canCall_remoteBean() {
		Assert.assertEquals("Hello World", this.getRemoteTestsRemote().greet("World"));
	}

	@Test
	public void containerTestContext_isNotActive_withoutContainerTest() {
		Assert.assertTrue(this.getRemoteTestsRemote().containerTestScopeIsNotActive());
	}

	@Test
	public void returnsDefaultScopedBean_ifContainerTestScopeNotActive() {
		int failures = 0;
		for (int i = 0; i < 100; i++) {
			try {
				this.testDefaultScopedBean();
			} catch (Exception ex) { // Catch error to get Assert-faults...
				logger.error("returnsDefaultScopedBean_ifContainerTestScopeNotActive failed", ex);
				failures++;
			}
		}

		if (failures > 0) {
			Assert.fail(String.format("Failed with %d failures", failures));
		}
	}

	private void testDefaultScopedBean() {
		int res = this.getRemoteTestsRemote().getScopedControlledRequest("defaultScoped");
		AssertUtil.assertEquals(1, res);
	}
}
