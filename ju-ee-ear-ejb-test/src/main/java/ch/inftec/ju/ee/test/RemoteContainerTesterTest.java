package ch.inftec.ju.ee.test;


/**
 * Convenience class to test RemoteContainerTest in combination with the
 * RemoteContainerTester object.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
public abstract class RemoteContainerTesterTest extends RemoteContainerTest {
	/**
	 * Tester class, i.e. the RemoteContainerTester implementation actually being used for
	 * this test case.
	 * @return
	 */
	protected abstract Class<?> getTesterClass();
	
	
	protected <T> T callRemoteMethod(String methodName) {
		return this.callRemoteMethod(
				methodName
				, new Class<?>[]{}
				, new Object[]{});
	}
	
	protected <T> T callRemoteMethod(String methodName, Class<?>[] paramTypes, Object[] params) {
		return this.runMethodInEjbContext(this.getTesterClass().getName()
				, methodName
				, paramTypes
				, params);
	}
	
	protected final void remoteSendSignal(int signal) {
		this.callRemoteMethod(
				"sendSignal"
				, new Class<?>[] {int.class}
				, new Object[] {signal});
	}
	
	protected final void remoteClearSignals() {
		this.callRemoteMethod(
				"clearSignals"
				, new Class<?>[] {}
				, new Object[] {});
	}
}
