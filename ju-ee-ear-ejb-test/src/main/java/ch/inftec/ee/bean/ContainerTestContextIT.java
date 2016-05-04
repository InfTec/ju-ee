package ch.inftec.ee.bean;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ee.bean.ContainerTestScopeProducer.MyString;
import ch.inftec.ju.ee.test.ContainerTest;

/**
 * ContainerTestContext related tests that run as ContainerTests.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class ContainerTestContextIT extends ContainerTest {
	@Test
	public void returnsContainerTestScopedBean() {
		int cnt1 = this.serviceLocator.cdi(RemoteTestsLocal.class).getScopedControlledRequest("containerTestScoped");
		Assert.assertEquals(1, cnt1);

		// Make sure we get the same bean when we look it up again...
		int cnt2 = this.serviceLocator.cdi(RemoteTestsLocal.class).getScopedControlledRequest("containerTestScoped");
		Assert.assertEquals(2, cnt2);
	}

	@Test
	public void canHandler_multipleBeans() {
		MyString s1 = this.serviceLocator.cdiComplex(MyString.class).named("containerTestScoped_string1").find().one();
		Assert.assertEquals("containerTestScoped_string1", s1.getValue());
		// Make sure we get the same instance for another lookup
		MyString s1b = this.serviceLocator.cdiComplex(MyString.class).named("containerTestScoped_string1").find().one();
		Assert.assertSame(s1, s1b);

		MyString s2 = this.serviceLocator.cdiComplex(MyString.class).named("containerTestScoped_string2").find().one();
		Assert.assertEquals("containerTestScoped_string2", s2.getValue());
		// Make sure we get the same instance for another lookup
		MyString s2b = this.serviceLocator.cdiComplex(MyString.class).named("containerTestScoped_string2").find().one();
		Assert.assertSame(s2, s2b);
	}
}
