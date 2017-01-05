package ch.inftec.ju.ee.test;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.test.TestRunnerFacade.TestRunnerContext;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import ch.inftec.ju.util.TestUtils;

/**
 * JUnit rule that runs a test method within the local JBoss context.
 * <p>
 * Note that this means that the method code will be run in a different JVM than the unit test itself,
 * so keep this in mind when using rules, fields and the like in your tests.
 * <p>
 * Explicitly supported features are:
 * <ul>
 *   <li>Expected exception: @Test(expected=ExpectedException.class)</li>
 * </ul>
 * @author Martin
 *
 */
public class ContainerTestRunnerRule implements TestRule {
	private Logger logger = LoggerFactory.getLogger(ContainerTestRunnerRule.class);

	private final TestRunnerType type;
	
	public enum TestRunnerType {
		/**
		 * Test method code and everything else runs in the container.
		 */
		CONTAINER,
		
		/**
		 * Test method code runs remotely, setup and verification runs on the server.
		 * Can be used for web tests (e.g. Selenium)
		 */
		REMOTE_TEST;
	}
	
	/**
	 * Creates a new test runner rule.
	 * <p>
	 * Type CONTAINER will run the test code in the container. Type REMOTE_TEST will run test
	 * code in a local JVM (not the container JVM) and just run the test setup and verification code in the container.
	 * @param type Running type
	 */
	public ContainerTestRunnerRule(TestRunnerType type) {
		this.type = type;
	}
	
	@Override
	public Statement apply(Statement base, Description description) {
		Method method = TestUtils.getTestMethod(description);
		
		Statement testStatement = null;

		// Create test context
		TestRunnerContext context = new TestRunnerContext();
		Path localRoot = Paths.get(".").toAbsolutePath();
		context.setLocalRoot(localRoot.toString());
		logger.info("New test context: " + context.getUuid());

		if (this.type == TestRunnerType.CONTAINER) {

			// Create the test statement, i.e. the statement that invokes the test method annotated
			// with @Test
			Statement containerTestRunnerStatement = new ContainerTestRunnerStatement(method, description, context);
			
			// Handle expected exception. We need to handle this explicitly as it is implemented
			// using a statement that we will discard here (as we don't use base)
			Test t = method.getAnnotation(Test.class);
			if (t != null && t.expected() != None.class) {
				testStatement = new ExpectException(containerTestRunnerStatement, t.expected());
			} else {
				testStatement = containerTestRunnerStatement;
			}
		} else {
			// With LOCAL_TEST, just run the original statement locally.
			testStatement = base;
		}
		
		
		// Wrap the testStatement in a ContainerVerifyRunnerStatement to handle
		// possible verification and post processing
		return new ContainerPreAndPostRunnerStatement(method, description, context, testStatement);
	}
	
	private static abstract class ContainerRunnerStatement extends Statement {
		protected final Method method;
		protected final Description description;
		protected final TestRunnerContext context;
		
		private ContainerRunnerStatement(Method method, Description description, TestRunnerContext context) {
			this.method = method;
			this.description = description;
			this.context = context;
		}
		
		@Override
		public void evaluate() throws Throwable {
			try {
				
				// try the new Rest approach
				this.doEvaluation(new RestTestRunnerFacade(), this.context);
				
				// Lookup TestRunnerFacadeBean with JNDI
				//this.doEvaluation(TestRunnerUtils.getTestRunnerFacade(), this.context);
			} catch (Throwable t) {
				throw RemoteUtils.getActualThrowable(t);
			}
		}
		
		protected abstract void doEvaluation(TestRunnerFacade facade, TestRunnerContext context) throws Throwable;
	}
	
	private static class ContainerTestRunnerStatement extends ContainerRunnerStatement {
		private ContainerTestRunnerStatement(Method method, Description description, TestRunnerContext context) {
			super(method, description, context);
		}

		@Override
		protected void doEvaluation(TestRunnerFacade facade, TestRunnerContext context) throws Throwable {
			facade.runTestMethodInEjbContext(new TestRunnerAnnotationHandler(method, description, context));
//			facade.runTestMethodInEjbContext(method.getDeclaringClass().getName(), method.getName(), context);
		}
	}
	
	private static class ContainerPreAndPostRunnerStatement extends ContainerRunnerStatement {
		private Logger logger = LoggerFactory.getLogger(ContainerPreAndPostRunnerStatement.class);

		private final Statement testStatement;
		
		private ContainerPreAndPostRunnerStatement(Method method, Description description, TestRunnerContext context,
				Statement testStatement) {
			super(method, description, context);
			
			this.testStatement = testStatement;
		}
		
		@Override
		protected void doEvaluation(TestRunnerFacade facade, TestRunnerContext context) throws Throwable {
			TestRunnerAnnotationHandler handler = new TestRunnerAnnotationHandler(method, description, context);
			
			// Run the pre actions
			SystemPropertyTempSetter tempSetter = facade.runPreTestActionsInEjbContext(handler);
			
			try {
				// Run the test statement first. We'll only run the verifiers if the test statement succeeds
				testStatement.evaluate();
				
				// Run the verifiers now
				facade.runPostTestActionsInEjbContext(handler);
			} finally {
				logger.debug("Cleaning up test run for context: " + handler.getUuid());
				facade.cleanupTestRun(handler, tempSetter);
			}
		}
	}
	

}