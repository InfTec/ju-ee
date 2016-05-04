package ch.inftec.ee.bean;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.cdi.ScopeControl;
import ch.inftec.ju.ee.test.ContainerTestContext;
import ch.inftec.ju.ee.test.ContainerTestScoped;
import ch.inftec.ju.ee.test.sim.RequestHolder;

public class ContainerTestScopeController {
	@ContainerTestScoped
	@Named("containerTestScoped")
	@ScopeControl // Only named leads to disambiguity
	@Produces
	private RequestHolder createContainerTestScopedHolder() {
		RequestHolder rh = new RequestHolder();
		rh.putRequest(String.class, "containerTestScoped");
		return rh;
	}

	@Named("defaultScoped")
	@ScopeControl // Only named leads to disambiguity
	@Produces
	private RequestHolder createDefaultScopedRequestHolder() {
		RequestHolder rh = new RequestHolder();
		rh.putRequest(String.class, "defaultScoped");
		return rh;
	}

	/**
	 * We'll need a separate class that acts as the actual producer to avoid cyclic dependencies as we need to
	 * have CDI inject our beans.
	 * 
	 * @author Martin Meyer <martin.meyer@inftec.ch>
	 * 
	 */
	static class Producer {
		private static Logger logger = LoggerFactory.getLogger(Producer.class);
		
		@Inject
		@Named("containerTestScoped")
		@ScopeControl
		private RequestHolder containerTestScopedHolder;

		@Inject
		@Named("defaultScoped")
		@ScopeControl
		private RequestHolder defaultScopedHolder;

		@Named("scopeControl")
		@ScopeControl
		// Only named leads to disambiguity
		@Produces
		public RequestHolder createRequestHandler() {
			if (ContainerTestContext.isContextActive()) {
				logger.info("Container test context active, returning container test scoped holder");
				return containerTestScopedHolder;
			} else {
				logger.info("Container test context inactive, returning default scoped holder");
				return defaultScopedHolder;
			}
		}

	}
}
