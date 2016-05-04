package ch.inftec.ee.cdi;
import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ee.cdi.CdiScopesIT.ScopeType;
import ch.inftec.ju.ee.cdi.DynamicCdiLoader;
import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.JuTestEnv;

public class DynamicCdiIT extends ContainerTest {
	@Test
	public void dynamicLoader_canBeResolved() {
		Assert.assertNotNull(dynamicLoader());
		Assert.assertSame(dynamicLoader(), dynamicLoader());
	}
	
	private DynamicCdiLoader dynamicLoader() {
		return this.serviceLocator.cdi(DynamicCdiLoader.class);
	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyDynamicCdi=-")
	public void canProduce_defaultScope_cdi() {
		MyDynamicCdi c1 = dynamicLoader().getImplementation(MyDynamicCdi.class);
		Assert.assertTrue(DefaultScopedDynamicCdi.class.isAssignableFrom(c1.getClass()));
	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyDynamicCdi=-")
	public void canProduce_defaultScope_cdi_usingAbstractClass() {
		MyAbstractDynamicCdi c1 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
		int instCnt1 = c1.getInstanceCnt();
		Assert.assertTrue(DefaultScopedAbstractDynamicCdi.class.isAssignableFrom(c1.getClass()));
		
		MyAbstractDynamicCdi c2 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
		
		CdiScopesIT.assertScopes(c1, c2, instCnt1, ScopeType.DEFAULT);
	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyAbstractDynamicCdi=factory")
	public void canProduce_defaultScope_cdi_fromFactory() {
		MyAbstractDynamicCdi c1 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
		Assert.assertEquals("factory", c1.getType());
		int instCnt1 = c1.getInstanceCnt();
		
		MyAbstractDynamicCdi c2 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
		
		CdiScopesIT.assertScopes(c1, c2, instCnt1, ScopeType.DEFAULT);
	}
	
	// Request scoped bean will not be mapped by @Any
//	@Test
//	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyAbstractDynamicCdi=request")
//	public void canProduce_requestScope_cdi_fromFactory() {
//		MyAbstractDynamicCdi c1 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
//		Assert.assertEquals("request", c1.getType());
//		int instCnt1 = c1.getInstanceCnt();
//		
//		MyAbstractDynamicCdi c2 = dynamicLoader().getImplementation(MyAbstractDynamicCdi.class);
//		
//		CdiScopesIT.assertScopes(c1, c2, instCnt1, ScopeType.DEFAULT);
//	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyScopeTest=request")
	public void canProduce_requestScope_cdi_fromFactory() {
		MyScopeTest c1 = this.serviceLocator.cdi(MyScopeTest.class);
		Assert.assertEquals("request", c1.type());
		int instCnt1 = c1.getInstanceCnt();
		
		MyScopeTest c2 = this.serviceLocator.cdi(MyScopeTest.class);
		
		CdiScopesIT.assertScopes(c1, c2, instCnt1, ScopeType.REQUEST);
	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyFactoryCdi=-")
	public void canProduce_cdi_fromDynamicCdiFabory() {
		AbstractCdi c1 = dynamicLoader().getImplementation(MyFactoryCdi.class);
		Assert.assertEquals("factoryDefault", c1.type());
	}
	
	@Test
	@JuTestEnv(systemProperties="ch.inftec.ee.cdi.MyFactoryCdi=factoryAlt")
	public void canProduce_cdi_fromDynamicCdiFabory_usingTag() {
		AbstractCdi c1 = dynamicLoader().getImplementation(MyFactoryCdi.class);
		Assert.assertEquals("factoryAlt", c1.type());
	}
}