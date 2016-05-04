package ch.inftec.webapp.ear.rest;

import java.util.Set;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.webtest.WebTestUtils;

/**
 * Test class to access RESTful service.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class RestTest {
	@Test
	public void canRead_fromHelloWorldResource() throws Exception {
		Response get = ClientBuilder.newBuilder().build()
				.target(WebTestUtils.getPageUrl("ju-ee-ear-web/rest/helloworld"))
				.request().get();

		String res = get.readEntity(String.class);

		Assert.assertEquals("Hello World", res);
	}

	/**
	 * Actually, I wanted to use header links, but couldn't get it to work yet...
	 */
	@Test
	public void doesNotExpose_links() {
		Response head = ClientBuilder.newBuilder().build()
				.target(WebTestUtils.getPageUrl("ju-ee-ear-web/rest/helloworld"))
				.request().head();

		Set<Link> links = head.getLinks();
		Assert.assertEquals(0, links.size());

	}
}
