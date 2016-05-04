package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.inftec.ee.cdi.MyContainerTestScopedBean;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContainerTestScopeIT extends ContainerTest {
	private static int initializations;
	private static int destructions;

	@Test
	public void _01_firstContainerTestScopedBean() {
		MyContainerTestScopedBean b1 = this.serviceLocator.cdi(MyContainerTestScopedBean.class);
		Assert.assertEquals("initial", b1.getName());
		Assert.assertEquals(0, b1.getNameChanges());
		b1.setName("newName");

		initializations = MyContainerTestScopedBean.getInititializations();
		destructions = MyContainerTestScopedBean.getDestructions();

		MyContainerTestScopedBean b2 = this.serviceLocator.cdi(MyContainerTestScopedBean.class);
		Assert.assertEquals("newName", b2.getName());
		Assert.assertEquals(1, b2.getNameChanges());

		// Shouldn't have created a new instance
		Assert.assertEquals(initializations, MyContainerTestScopedBean.getInititializations());
		Assert.assertEquals(destructions, MyContainerTestScopedBean.getDestructions());
	}

	// Depends on _01_firstContainerTestScopedBean
	@Test
	public void _02_secondContainerTestScopedBean() {
		// One bean should have been destroyed
		Assert.assertEquals(initializations, MyContainerTestScopedBean.getInititializations());
		Assert.assertEquals(destructions + 1, MyContainerTestScopedBean.getDestructions());

		MyContainerTestScopedBean b1 = this.serviceLocator.cdi(MyContainerTestScopedBean.class);
		Assert.assertEquals("initial", b1.getName());
		Assert.assertEquals(0, b1.getNameChanges());

		// Another one should have been initialized
		Assert.assertEquals(initializations + 1, MyContainerTestScopedBean.getInititializations());
		Assert.assertEquals(destructions + 1, MyContainerTestScopedBean.getDestructions());
	}
}
