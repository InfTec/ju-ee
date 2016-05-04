package ch.inftec.ju.ee.client;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import ch.inftec.ju.ee.cdi.ScopeControl;

/**
 * Producer used for CDI modifier testing.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
public class ModifierTestProducer {
	/**
	 * Test Object that can be used to verify we got the right object.
	 * 
	 * @author martin.meyer@inftec.ch
	 *
	 */
	public interface TestObject {
		String getValue();
	}
	
	@Named("named")
	@Produces
	public TestObject createNamed() {
		return this.createTestObject("named");
	}
	
	@Named("namedScope")
	@Produces
	@ScopeControl
	public TestObject createNamedWithScopeControl() {
		return this.createTestObject("namedWithScopeControl");
	}
	
	@Produces
	@ScopeControl
	public TestObject createWithScopeControl() {
		return this.createTestObject("scopeControl");
	}
	
	private TestObject createTestObject(final String val) {
		return new TestObject() {
			@Override
			public String getValue() {
				return val;
			}
		};
	}
}
