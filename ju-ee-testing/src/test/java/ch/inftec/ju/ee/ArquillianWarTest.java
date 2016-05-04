package ch.inftec.ju.ee;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.test.AbstractTestBean;

@RunWith(Arquillian.class)
public class ArquillianWarTest {
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");

		war.addClass(AbstractTestBean.class);

		// Enable CDI support
		war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return war;
	}

	@Inject
	private AbstractTestBean bean;

	@Test
	public void bean_isDeployed_andInjected() {
		assertNotNull(bean.getGreeting("World"));
	}
}
