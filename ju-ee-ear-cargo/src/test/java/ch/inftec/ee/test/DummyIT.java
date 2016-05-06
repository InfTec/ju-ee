package ch.inftec.ee.test;

import org.junit.Test;

/**
 * Failsafe plugin won't notice errors in dependencies if the project doesn't have at least
 * one test itself.
 * @author Martin
 *
 */
public class DummyIT {
	@Test
	public void emptyTest_toForceFailsafeVerify() {
	}
}
