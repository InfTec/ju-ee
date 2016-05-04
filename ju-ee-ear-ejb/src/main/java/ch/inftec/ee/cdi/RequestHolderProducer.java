package ch.inftec.ee.cdi;

import javax.enterprise.inject.Produces;

import ch.inftec.ju.ee.test.ContainerTestScoped;
import ch.inftec.ju.ee.test.sim.RequestHolder;

/**
 * Producer for RequestHolder, used in RequestHolderIT.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class RequestHolderProducer {
	@ContainerTestScoped
	@Produces
	public RequestHolder createRequestHolder() {
		return new RequestHolder();
	}
}
