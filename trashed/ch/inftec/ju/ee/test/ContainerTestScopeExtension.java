package ch.inftec.ju.ee.test;

import java.io.Serializable;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI extension to register ContainerTestScope.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class ContainerTestScopeExtension implements Extension, Serializable {
	private Logger logger = LoggerFactory.getLogger(ContainerTestScopeExtension.class);

	public void addScope(@Observes final BeforeBeanDiscovery event) {
		logger.debug("Adding Scope ContainerTestScoped");
		event.addScope(ContainerTestScoped.class, true, false);
	}

	public void registerContext(@Observes final AfterBeanDiscovery event) {
		logger.debug("Adding Context ContainerTestContext");
		event.addContext(new ContainerTestContext());
	}
}