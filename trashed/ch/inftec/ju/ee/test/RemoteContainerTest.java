package ch.inftec.ju.ee.test;

import org.junit.Before;
import org.junit.Rule;

import ch.inftec.ju.ee.client.JndiServiceLocator;
import ch.inftec.ju.ee.test.ContainerTestRunnerRule.TestRunnerType;
import ch.inftec.ju.util.JuRuntimeException;

/**
 * Base class for tests that run locally, but require access to a remote container, e.g.
 * by using a remote facade or a web service.
 * <p>
 * This class supports all the JU container and DB test annotations like @DataSet, @DataSetExport and
 * the like.
 * @author martin.meyer@inftec.ch
 *
 */
public class RemoteContainerTest {
	/**
	 * Rule that performs test setup and verification on the server.
	 */
	@Rule
	public ContainerTestRunnerRule testRunnerRule = new ContainerTestRunnerRule(TestRunnerType.REMOTE_TEST);
	
	 /**
     * Remote ServiceLocator instance that can be used to lookup JNDI or CDI objects on the server.
     */
    protected JndiServiceLocator serviceLocator;
    
    @Before
    public void init() {
    	this.serviceLocator = TestRunnerUtils.getRemoteServiceLocator();
    	this.testRunnerFacade = TestRunnerUtils.getTestRunnerFacade();
    }
    
    /**
	 * Runs a parameterless method in an EJB context and returns the result of the method.
	 * @param className Class name that contains the method
	 * @param methodName Method name
	 * @return Result value of the method
	 */
	protected final <T> T runMethodInEjbContext(String className, String methodName) {
		try {
			@SuppressWarnings("unchecked")
			T res = (T) testRunnerFacade.runMethodInEjbContext(className, methodName, new Class<?>[] {}, new Object[] {});
			return res;
		} catch (Exception ex) {
			throw new JuRuntimeException("Couldn't run method in EjbContext", ex);
		}
	}
    
	/**
	 * Runs an arbitrary method in an EJB context and returns the result of the method.
	 * @param className Class name that contains the method
	 * @param methodName Method name
	 * @param parameterTypes Array of argument types
	 * @param args Array of arguments
	 * @return Result value of the method
	 */
	protected final <T> T runMethodInEjbContext(String className, String methodName, Class<?> parameterTypes[], Object args[]) {
		try {
			@SuppressWarnings("unchecked")
			T res = (T) testRunnerFacade.runMethodInEjbContext(className, methodName, parameterTypes, args);
			return res;
		} catch (Exception ex) {
			Throwable actualThrowable = RemoteUtils.getActualThrowable(ex);
			throw new JuRuntimeException("Couldn't run method in EJB context: %s"
					, actualThrowable
					, actualThrowable == null ? null : actualThrowable.getMessage());
		}
	}
    
    /**
     * Instance of a (remotely available) TestRunnerFacade.
     */
    protected TestRunnerFacade testRunnerFacade;
}
