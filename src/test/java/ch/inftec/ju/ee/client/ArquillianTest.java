package ch.inftec.ju.ee.client;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.test.AbstractTestBean;

@RunWith(Arquillian.class)
public class ArquillianTest {
	@Deployment
	public static WebArchive createDeployment() {
		return null;
	}

	@Inject
	private AbstractTestBean bean;

	@Test
	public void bean_isDeployed_andInjected() {
		assertNotNull(bean.getGreeting("World"));
	}
}
