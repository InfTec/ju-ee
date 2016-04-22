package ch.inftec.ju.ee.test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.ejb.Remote;

import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.util.SystemPropertyTempSetter;

/**
 * Helper facade to invoke (test) methods from local unit tests in a remote JVM server container
 * context.
 * <p>
 * The facade is supposed to perform regular EJB transaction commit or rollback, depending on the
 * exception that might arise when executing test methods.
 * @author Martin
 *
 */
@Remote
public interface TestRunnerFacade {
	/**
	 * Gets the version of the TestRunnerFacade.
	 * <p>
	 * This can be used for remote lookup testing purposes.
	 * @return The actual version of the TestRunnerFacade.
	 */
	public String getVersion();
	
	/**
	 * Runs pre test actions (like data set loading) in an EJB context.
	 * @param handler TestRunner handler providing information about the test class and method
	 * @return SystemPropertyTempSetter instance that contains the original system property state. At the end of the test
	 * (be it successful of not) we need to call the 
	 * @throws Exception If the actions fail
	 */
	public SystemPropertyTempSetter runPreTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception;
	
	/**
	 * Runs a (test) method in an EJB context, allowing it to use container functionality and
	 * beans.
	 * @param handler TestRunner handler that provides information about the test class and method
	 * @throws Exception If the method fails with an exception (including test assertion failures)
	 */
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) throws Exception;

	/**
	 * Runs post test actions (like data set exports and data verifies) in an EJB context.
	 * @param handler TestRunner handler providing information about the test class and method
	 * @throws Exception If the actions fail
	 */
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception;
	
	/**
	 * Resets the system properties to their original state and ends the test context on the server.
	 * <p>
	 * Must be called with the SystemPropertyTempSetter returned by the runPreTestActionInEjbContext method
	 * 
	 * @param handler
	 *            TestRunner handler providing information about the test class and method
	 * @param tempSetter
	 *            SystemPropertyTempSetter that is used to restore the system property to their original (i.e. pre test)
	 *            state
	 */
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter);
	
	/**
	 * Runs an arbitrary method in an EJB context and returns the result of the method.
	 * @param className Class name that contains the method
	 * @param methodName Method name
	 * @param parameterTypes Array of argument types
	 * @param args Array of arguments
	 * @return Result value of the method
	 */
	public Object runMethodInEjbContext(String className, String methodName, Class<?> parameterTypes[], Object args[]) throws Exception;
	
	/**
	 * Helper class that contains information about the context the test runs within.
	 * @author Martin
	 *
	 */
	public static class TestRunnerContext implements Serializable {
		private String localRoot;
		private UUID uuid;

		public TestRunnerContext() {
			uuid = UUID.randomUUID();
		}

		/**
		 * Gets the root path of the local (i.e. not server) test. This can be used to
		 * create files in a location relative to the test class VM rather than the JBoss server.
		 * @return Root path of the local test execution VM
		 */
		public String getLocalRoot() {
			return localRoot;
		}

		public void setLocalRoot(String localRoot) {
			this.localRoot = localRoot;
		}

		/**
		 * Gets a unique ID identifying the test run. This can be used to correlate multiple remote calls
		 * to the same test run.
		 * 
		 * @return Universal identifier identifying this test context
		 */
		public UUID getUuid() {
			return uuid;
		}
	}
	
	/**
	 * Interface for classes that are context aware, i.e. allow for a TestRunnerContext to be
	 * set when executed by the TestRunnerFacade.
	 * @author Martin
	 *
	 */
	public interface ContextAware {
		/**
		 * Sets the TestRunnerContext
		 * @param context
		 */
		void setContext(TestRunnerContext context);
	}
	
	/**
	 * Interface for classes that are transaction aware, i.e. that are prepared to work
	 * with a TxHandler
	 * @author Martin Meyer <martin.meyer@inftec.ch>
	 *
	 */
	public interface TransactionAware {
		/**
		 * Sets the TxHandler used to control the transaction.
		 * @param txHandler TxHandler
		 */
		void setTxHandler(TxHandler txHandler);
	}
	
	/**
	 * Interfaces for classes that would like to be initialized before the test method is run
	 * (some functionality as a @Before method would provide).
	 * @author Martin
	 *
	 */
	public interface Initializable {
		/**
		 * Initializer method that is called before the unit test is executed.
		 * <p>
		 * The method will be called within the same EJB / transaction context as the test method
		 */
		void init();
	}
	
	/**
	 * Helper object that contains information to instantiate a DataVerifier.
	 * <p>
	 * Info consists of a class name and an optional list of parameters that will be passed to
	 * the constructor of the DataVerifier.
	 * @author Martin
	 *
	 */
	public static class DataVerifierInfo implements Serializable {
		private final String className;
		private final List<Object> parameters;
		
		public DataVerifierInfo(String className, List<Object> parameters) {
			this.className = className;
			this.parameters = parameters == null ? Collections.emptyList() : parameters;
		}
		
		public String getClassName() {
			return className;
		}
		
		public List<Object> getParameters() {
			return parameters;
		}
	}
}
