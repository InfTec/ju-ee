package ch.inftec.ju.ee;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.test.AbstractTestBean;

@RunWith(Arquillian.class)
public class ArquillianJarTest {
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar");

		jar.addClass(AbstractTestBean.class);
		jar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return jar;
	}

	@Inject
	private AbstractTestBean bean;

	@Test
	public void bean_isDeployed_andInjected() {
		assertNotNull(bean.getGreeting("World"));
	}
}
