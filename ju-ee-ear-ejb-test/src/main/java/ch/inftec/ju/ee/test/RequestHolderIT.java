package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.sim.RequestHolder;

/**
 * Integration Test to make sure we can use the RequestHolder in a ContainerTest context with ContainerTestScope.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class RequestHolderIT extends ContainerTest {
	@Test
	public void canUse_requestHolder_withContainerTestScope() {
		this.testRequestHolder();
	}

	@Test
	public void canUse_requestHolder_withContainerTestScope2() {
		this.testRequestHolder();
	}

	private void testRequestHolder() {
		RequestHolder rh = this.serviceLocator.cdi(RequestHolder.class);

		Assert.assertNull(rh.pollRequest(String.class));

		rh.putRequest(String.class, "test");
	}
}
