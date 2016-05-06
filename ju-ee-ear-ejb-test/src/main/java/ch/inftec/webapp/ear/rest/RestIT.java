package ch.inftec.webapp.ear.rest;

import org.jboss.resteasy.client.ClientRequest;
import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.ee.test.UrlTestUtils;

/**
 * Test class to access a RESTful service from within JBoss. This makes sure that
 * the application server contains the correct dependencies to call a RESTful service.
 * <p>
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class RestIT extends ContainerTest {
	@Test
	public void canRead_fromHelloWorldResource() throws Exception {
		ClientRequest req = new ClientRequest(UrlTestUtils.getPageUrl("ju-ee-ear-web/rest/helloworld"));
		String res = req.get(String.class).getEntity();

		Assert.assertEquals("Hello World", res);
	}
}
