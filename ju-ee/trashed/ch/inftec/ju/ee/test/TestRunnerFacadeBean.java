package ch.inftec.ju.ee.test;

import java.lang.reflect.Method;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import ch.inftec.ju.db.JuEmUtil;
import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler.ContainerTestContextSetter;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.SystemPropertyTempSetter;

/**
 * Helper bean to run container tests in the container.
 * <p>
 * We'll use bean managed transaction management to control the transactions better.
 * @author Martin
 *
 */
@TransactionManagement(TransactionManagementType.BEAN)
public class TestRunnerFacadeBean implements TestRunnerFacade {
	private static Logger logger = Logger.getLogger(TestRunnerFacadeBean.class);
	
	@Inject
	private UserTransaction tx;

	@Inject
	private EntityManager em;
//	@Inject
//	private DateProvider dateProvider;

	@Override
	public SystemPropertyTempSetter runPreTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter()) {
			SystemPropertyTempSetter tempSetter = handler.initContainerTestEnv();

			try (TxHandler txHandler = new TxHandler(this.tx, true)) {
				// Execute post test annotations (dataset exporting, data verifying)
				handler.executePreTestAnnotations(new JuEmUtil(this.em));
				txHandler.commit(); // Commit after data verifying / exporting
			} catch (Exception ex) {
				// Reset properties in case of an exception
				tempSetter.close();
				throw ex;
			}

			return tempSetter;
		}
	}
	
	@Override
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter()) {
			try (TxHandler txHandler = new TxHandler(this.tx, true)) {
				logger.debug(String.format("Running Test %s", handler));
				// this.dateProvider.resetProvider();

				// Run the test method
				handler.executeTestMethod(txHandler);

				txHandler.commit(); // Perform a commit after the execution of the test method
			}
		}
	}
	
	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter()) {
			// Run post server annotations in an own annotation so any changed made there is available in the export / verifying phase
			try (TxHandler txHandler = new TxHandler(this.tx, true)) {
				// Execute post test annotations (dataset exporting, data verifying)
				handler.executePostServerCode(new JuEmUtil(this.em));
				txHandler.commit(); // Commit after data verifying / exporting
			}

			// Run post test annotations (export, verify)
			try (TxHandler txHandler = new TxHandler(this.tx, true)) {
				// Execute post test annotations (dataset exporting, data verifying)
				handler.executePostTestAnnotations(new JuEmUtil(this.em));
				txHandler.commit(); // Commit after data verifying / exporting
			}
		}
	}


	@Override
	public Object runMethodInEjbContext(String className, String methodName,
			Class<?> argumentTypes[], Object[] args) throws Exception {
		try (TxHandler txHandler = new TxHandler(this.tx, true)) {
			Class<?> clazz = Class.forName(className);
			Object instance = clazz.newInstance();
			
			Method method = clazz.getMethod(methodName, argumentTypes);
			Object res = method.invoke(instance, args);
			
			txHandler.commit();
			return res;
		}
	}

	@Override
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter(true)) {
			tempSetter.close();

			// Clear property chain if clearing property is set
			if (JuUtils.getJuPropertyChain().get("ju-testing-ee.clearPropertyChainAfterEachTest", Boolean.class, "false")) {
				JuUtils.clearPropertyChain();
			}
		}
	}

	@Override
	public String getVersion() {
		return "1";
	}
}
