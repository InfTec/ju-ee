package ch.inftec.ee.cdi;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import ch.inftec.ju.ee.cdi.DynamicCdiLoader;

/**
 * Test of a producer that will lookup a dynamic bean and provide it with scope Request.
 * @author martin.meyer@inftec.ch
 *
 */
public class MyScopeTestProducer {
	@Inject
	private DynamicCdiLoader loader;
	
	@Produces @RequestScoped
	public MyScopeTest createMyScopeTest() {
		return this.loader.getImplementation(MyScopeTest.class);
	}
}
