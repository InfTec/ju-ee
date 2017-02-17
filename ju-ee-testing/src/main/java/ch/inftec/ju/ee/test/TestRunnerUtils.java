package ch.inftec.ju.ee.test;

import ch.inftec.ju.ee.client.JndiServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;

/**
 * Contains helper functions related to remote tests.
 * <p>
 * Provides cached instances of RemoteServiceLocator and TestRunnerFacade. All classes requiring access to these instances
 * should use the cached instances to optimize resource usage.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
public class TestRunnerUtils {
	private static JndiServiceLocator remoteServiceLocator;
	private static TestRunnerFacade testRunnerFacade;
	
	/**
	 * Gets an instance of a remote JndiServiceLocator, configured by JU property files.
	 * @return
	 */
	public static synchronized JndiServiceLocator getRemoteServiceLocator() {
		if (remoteServiceLocator == null) {
			remoteServiceLocator = ServiceLocatorBuilder.createRemoteByConfigurationFiles();
		}
		return remoteServiceLocator;
	}
}
