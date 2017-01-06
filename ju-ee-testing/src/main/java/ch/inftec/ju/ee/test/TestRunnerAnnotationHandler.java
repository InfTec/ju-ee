package ch.inftec.ju.ee.test;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.runner.Description;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.ee.test.TestRunnerFacade.ContextAware;
import ch.inftec.ju.ee.test.TestRunnerFacade.Initializable;
import ch.inftec.ju.ee.test.TestRunnerFacade.TestRunnerContext;
import ch.inftec.ju.ee.test.TestRunnerFacade.TransactionAware;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DbTestAnnotationHandler;

/**
 * Helper class to handle test annotations like @DataSet and @DataVerify.
 * <p>
 * The handler is serializable so it can be used for container tests that run code in different JVMs.
 * <p>
 * When calling the execute... methods, the client is responsible that a valid transaction is present.
 * @author Martin
 *
 */

public class TestRunnerAnnotationHandler extends DbTestAnnotationHandler implements Serializable {
	private final TestRunnerContext context;
	
	TestRunnerAnnotationHandler(Method method, Description description, TestRunnerContext context) {
		super(method, description);
		
		this.context = context;
	}
	
	TestRunnerContext getContext() {
		return context;
	}
	
	class ContainerTestContextSetter implements AutoCloseable {
		boolean disposeContext = false;

		public ContainerTestContextSetter() {
			ContainerTestContext.startContext(getContext() != null
					? getContext().getUuid()
					: null);
		}

		public ContainerTestContextSetter(boolean disposeContext) {
			this();

			this.disposeContext = disposeContext;
		}

		@Override
		public void close() {
			ContainerTestContext.endContext(this.disposeContext);
		}

	}

	UUID getUuid() {
		return this.getContext() != null
				? this.getContext().getUuid()
				: null;
	}

	@Override
	protected String getLocalRoot() {
		return this.getContext().getLocalRoot();
	}
	
//	@Override
	private void initTestClass(Object instance, TxHandler txHandler) {
		// Try to set the context (if the class is ContextAware)
		if (ContextAware.class.isAssignableFrom(instance.getClass())) {
			((ContextAware) instance).setContext(this.getContext());
		}
		
		if (TransactionAware.class.isAssignableFrom(instance.getClass())) {
			((TransactionAware) instance).setTxHandler(txHandler);
		}
		
		// Try to call the init method (if the class implements Initializable)
		if (Initializable.class.isAssignableFrom(instance.getClass())) {
			((Initializable) instance).init();
		}
	}
	
	@Override
	protected void initVerifier(DataVerifier verifier) {
		if (verifier instanceof DataVerifierCdi) {
			((DataVerifierCdi) verifier).init(ServiceLocatorBuilder.buildLocal().createServiceLocator());
		}
	}
	
	/**
	 * Runs the actual test method.
	 * <p>
	 * This is intended to be used by remote tests that need to run the test method in another VM as the JUnit test runs.
	 * @throws Exception
	 */
	public final void executeTestMethod(TxHandler txHandler) throws Exception {
		// Get an instance of the test method so we can invoke it
		Class<?> clazz = Class.forName(this.testClassName);
		Object instance = clazz.newInstance();
		Method method = clazz.getMethod(this.testMethodName);
		
		this.initTestClass(instance, txHandler);
		
		// Invoke the method
		try {
			method.invoke(instance);
		} catch (Exception ex) {
			this.handleServerThrowable(ex);
		}
	}
}
