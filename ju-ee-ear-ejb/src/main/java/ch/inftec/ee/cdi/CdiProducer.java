package ch.inftec.ee.cdi;

import javax.enterprise.inject.Produces;

import ch.inftec.ju.ee.test.cdi.JarDefaultScopeCdi;

public class CdiProducer {
	/**
	 * Needed for the ServiceLocatorIT test case
	 */
	@Produces
	private JarDefaultScopeCdi jarDefaultScopeCdi = new JarDefaultScopeCdi();
}
