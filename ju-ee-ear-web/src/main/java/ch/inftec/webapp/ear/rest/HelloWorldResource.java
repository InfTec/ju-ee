package ch.inftec.webapp.ear.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.links.LinkResource;

/**
 * See PR-24.
 * 
 * Service will be exposed at ju-ee-ear-web/rest/helloworld
 * 
 * @author martin.meyer@inftec.ch
 * 
 */
@Path("helloworld")
public interface HelloWorldResource {
	// Links not really working yet...
	@LinkResource

	@GET
	@Produces("text/plain")
	public String getClickedMessage();
}
