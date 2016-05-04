package ch.inftec.webapp.ear.rest;


/**
 * See PR-24.
 * 
 * Service will be exposed at ju-ee-ear-web/rest/helloworld
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class HelloWorldResourceBean implements HelloWorldResource {
	// @GET
	// @Path("/")
	// @Produces("text/xml")
	// public HelloWorldResource getServiceInfo() {
	// return new HelloWorldResource();
	// }

	public String getClickedMessage() {
		return "Hello World";
	}
}
