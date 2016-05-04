package ch.inftec.ju.ee.client;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import ch.inftec.ee.cdi.DefaultScopedCdi;
import ch.inftec.ju.ee.test.ContainerTestRunnerRule;
import ch.inftec.ju.ee.test.ContainerTestRunnerRule.TestRunnerType;
import ch.inftec.ju.ee.test.TestLocal;
import ch.inftec.ju.ee.test.TestNoInterfaceBean;
import ch.inftec.ju.ee.test.TestRemote;
import ch.inftec.ju.ee.test.cdi.JarDefaultScopeCdi;

public class ServiceLocatorIT {
	@Rule
	public ContainerTestRunnerRule testRunnerRule = new ContainerTestRunnerRule(TestRunnerType.CONTAINER);
	
	@Test
	public void localServiceLocator_returnsNoInterfaceBean() {
		ServiceLocator serviceLocator = ServiceLocatorBuilder.buildLocal().createServiceLocator();
		
		TestNoInterfaceBean b = serviceLocator.cdi(TestNoInterfaceBean.class);
		Assert.assertEquals("TestNoInterfaceBean says hello to NoInterface", b.getGreeting("NoInterface"));
	}
	
	@Test
	public void localServiceLocator_returnsLocalFacade() {
		ServiceLocator serviceLocator = ServiceLocatorBuilder.buildLocal().createServiceLocator();
		
		TestLocal b = serviceLocator.cdi(TestLocal.class);
		Assert.assertEquals("TestLocalBean says hello to Local", b.getGreeting("Local"));
	}
	
	@Test
	public void localServiceLocator_returnsRemoteFacade() {
		ServiceLocator serviceLocator = ServiceLocatorBuilder.buildLocal()
				.moduleName("ju-ee-ear-ejb")
				.createServiceLocator();
		
		TestRemote b = serviceLocator.lookup(TestRemote.class);
		Assert.assertEquals("TestRemoteBean says hello to Remote", b.getGreeting("Remote"));
	}
	
	@Test
	public void localServiceLocator_returnsEntityManager() {
		ServiceLocator serviceLocator = ServiceLocatorBuilder.buildLocal().createServiceLocator();
		
		EntityManager em = serviceLocator.cdi(EntityManager.class);
		Assert.assertNotNull(em);
	}
	
	@Test
	public void finds_unannotatedClass() {
		DefaultScopedCdi c = this.local().cdi(DefaultScopedCdi.class);
		Assert.assertNotNull(c);
	}
	
	/**
	 * Note that we need to specify a producer in order for Weld to find that bean.
	 * See {@link ch.inftec.ee.cdi.CdiProducer}.
	 */
	@Test
	public void finds_unannotatedClass_inNonWeldJar() {
		JarDefaultScopeCdi c = this.local().cdi(JarDefaultScopeCdi.class);
		Assert.assertNotNull(c);
	}
	
	private ServiceLocator local() {
		return ServiceLocatorBuilder.buildLocal().createServiceLocator();
	}
}
