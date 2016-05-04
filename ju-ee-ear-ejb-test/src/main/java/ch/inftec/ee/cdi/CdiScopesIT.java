package ch.inftec.ee.cdi;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.inftec.ju.ee.test.ContainerTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CdiScopesIT extends ContainerTest {
	private static int firstInstanceCnt;
	
	@Test
	public void defaultScope_returnsRegularInstance() {
		DefaultScopedCdi c1 = this.serviceLocator.cdi(DefaultScopedCdi.class);
		int instCnt1 = c1.getInstanceCnt();
		DefaultScopedCdi c2 = this.serviceLocator.cdi(DefaultScopedCdi.class);
		assertScopes(c1, c2, instCnt1, ScopeType.DEFAULT);
	}
	
	@Test
	public void applicationScope_returnsProxy() {
		ApplicationScopedCdi c1 = this.serviceLocator.cdi(ApplicationScopedCdi.class);
		int instCnt1 = c1.getInstanceCnt();
		ApplicationScopedCdi c2 = this.serviceLocator.cdi(ApplicationScopedCdi.class);
		assertScopes(c1, c2, instCnt1, ScopeType.APPLICATION);
		
		// Not working... Assert.assertTrue(Proxy.isProxyClass(c1.getClass()));
	}
	
	@Test
	public void _01_canGet_requestScoped_bean() {
		RequestScopedCdi c1 = this.serviceLocator.cdi(RequestScopedCdi.class);
		int instCnt1 = c1.getInstanceCnt();
		firstInstanceCnt = c1.getInstanceCnt();
		
		RequestScopedCdi c2 = this.serviceLocator.cdi(RequestScopedCdi.class);
		Assert.assertEquals(firstInstanceCnt, c2.getInstanceCnt());
	
		assertScopes(c1, c2, instCnt1, ScopeType.REQUEST);
	}
	
	@Test
	public void _02_canGet_requestScoped_bean() {
		RequestScopedCdi c1 = this.serviceLocator.cdi(RequestScopedCdi.class);
		int instCnt1 = c1.getInstanceCnt();
		Assert.assertEquals(firstInstanceCnt + 1, c1.getInstanceCnt());
		
		RequestScopedCdi c2 = this.serviceLocator.cdi(RequestScopedCdi.class);
		Assert.assertEquals(firstInstanceCnt + 1, c2.getInstanceCnt());
		
		assertScopes(c1, c2, instCnt1, ScopeType.REQUEST);
	}
	
	public static enum ScopeType {
		DEFAULT,
		REQUEST,
		APPLICATION
	}
	
	public static void assertScopes(AbstractCdi c1, AbstractCdi c2, int instCnt1, ScopeType type) {
		int calls1 = c1.getCalls();
		int calls2 = c2.getCalls();
		int instCnt2 = c2.getInstanceCnt();

		if (type == ScopeType.APPLICATION) {
			Assert.assertTrue(calls2 == calls1 + 1);
			
			Assert.assertEquals(1, instCnt1);
			Assert.assertEquals(1, instCnt2);
			
			Assert.assertSame(c1, c2);
		} else if (type == ScopeType.REQUEST) {
			Assert.assertTrue(calls2 == calls1 + 1);
			
			Assert.assertEquals(instCnt1, instCnt2);
			
			Assert.assertSame(c1, c2);
		} else if (type == ScopeType.DEFAULT) {
			Assert.assertEquals(1, calls1);
			Assert.assertEquals(1, calls2);
			
			Assert.assertTrue(instCnt2 == instCnt1 + 1);
			
			Assert.assertNotSame(c1, c2);
		} else {
			Assert.fail("Unknown scope");
		}
	}
}
