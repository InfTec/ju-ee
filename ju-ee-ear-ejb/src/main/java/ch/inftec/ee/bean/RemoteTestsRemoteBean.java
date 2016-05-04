package ch.inftec.ee.bean;

import javax.ejb.Stateless;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.inject.Named;

import ch.inftec.ju.ee.cdi.ScopeControl;
import ch.inftec.ju.ee.client.ServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.ee.test.ContainerTestContext;
import ch.inftec.ju.ee.test.sim.RequestHolder;
import ch.inftec.ju.util.AssertUtil;

@Stateless
public class RemoteTestsRemoteBean implements RemoteTestsRemote, RemoteTestsLocal {
	@Inject
	private RequestHolder rh;

	/*
	 * Note that injecting a scope controlled bean like this does not work if the scopes change as the
	 * stateless bean may (and most probably) will be cached. If we get a bean with default scope then, the reference
	 * will stay the same for multiple calls.
	 * 
	 * Instead, we need to explicitly lookup the bean trough the BeanManager (using ServiceLocator) to foce the
	 * lookup (and maybe change of Scope).
	 */
	@Inject
	@Named("scopeControl")
	@ScopeControl
	private RequestHolder rhScopeControl;

	@Override
	public String greet(String name) {
		return "Hello " + name;
	}

	@Override
	public boolean containerTestScopeIsNotActive() {
		AssertUtil.assertFalse(ContainerTestContext.isContextActive());

		try {
			rh.pollRequest(Object.class);
			AssertUtil.fail("Expected ContextNotActiveException");
		} catch (ContextNotActiveException ex) {
			// Expected
		}

		return true;
	}

	@Override
	public int getScopedControlledRequest(String expectedString) {
		 // Explicit lookup of bean. See comment for this.rhScopeControl
		
		ServiceLocator sl = ServiceLocatorBuilder.buildLocal().createServiceLocator();
		RequestHolder rhScopeControl = sl.cdiComplex(RequestHolder.class)
			.named("scopeControl")
			.scopeControl()
			.find().one();
		
		AssertUtil.assertEquals(expectedString, rhScopeControl.peekRequest(String.class));
		rhScopeControl.putRequest(Integer.class, 1);
		return rhScopeControl.getRequestCount(Integer.class);
	}
}
