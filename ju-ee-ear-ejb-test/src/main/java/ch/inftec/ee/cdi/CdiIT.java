package ch.inftec.ee.cdi;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;

public class CdiIT extends ContainerTest {
	@Test
	public void canLookup_intance_ofSomeInterface() {
		SomeInterface si = this.serviceLocator.cdi(SomeInterface.class);
		Assert.assertEquals("Some Implemenation", si.getValue());
	}
}
