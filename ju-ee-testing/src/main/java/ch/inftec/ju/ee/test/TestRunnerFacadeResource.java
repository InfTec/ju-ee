package ch.inftec.ju.ee.test;

import java.lang.reflect.Method;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionScoped;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.inftec.ju.db.JuEmUtil;
import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler.ContainerTestContextSetter;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.RequestScoped;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TestRunnerFacadeResource implements TestRunnerFacade {

	private Logger logger = Logger.getLogger(TestRunnerFacadeResource.class);
	
	@Inject
	private UserTransaction tx;
	
	@Inject
	private EntityManager em;
	
	@Path("version")
	@GET
	@Override
	public String getVersion() {
		logger.info("version");
		return "1";
	}

	@Path("preTestActions")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Override
	public SystemPropertyTempSetter runPreTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		
		logger.info("execute Pre run Actions");

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

	@Path("runMethodinEjb")
	@POST
	@Consumes("application/json")
	@Override
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) {
		logger.info("run Test Method in EJB Context");
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter()) {
			try (TxHandler txHandler = new TxHandler(this.tx, true)) {
				logger.debug(String.format("Running Test %s", handler));
				// this.dateProvider.resetProvider();

				// Run the test method
				handler.executeTestMethod(txHandler);

				txHandler.commit(); // Perform a commit after the execution of the test method
			} catch (Throwable t) {
				throw wrapThrowable(t);
			}
		}
		
	}
	
	private RuntimeException wrapThrowable(Throwable originalThrowable) {
		return new TestRunnerFacadeWrappingException(originalThrowable);
	}

	@Path("postTestActions")
	@POST
	@Consumes("application/json")
	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		logger.info("execute post run Actions");
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

	// indirect call of method
	// since Rest allows only one Json Object per requestbody, the parameters are encapsulated in a helper class
	@Path("cleanupTestRun")
	@POST
	@Consumes("application/json")
	public void cleanupTestRun(CleanupRestParamEncapsulationObject params) {
		cleanupTestRun(params.handler,params.tempSetter);
	}

	@Override
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		logger.info("cleanup test run");	
		try (ContainerTestContextSetter s = handler.new ContainerTestContextSetter(true)) {
			tempSetter.close();

			// Clear property chain if clearing property is set
			if (JuUtils.getJuPropertyChain().get("ju-testing-ee.clearPropertyChainAfterEachTest", Boolean.class, "false")) {
				JuUtils.clearPropertyChain();
			}
		}
	}

	// indirect call of method
	// since Rest allows only one Json Object per requestbody, the parameters are encapsulated in a helper class
	@Path("runMethod2")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Object runMethodInEjbContext(RunMethodRestParamEncapsulationObject params) throws Exception {
		return runMethodInEjbContext(params.className,params.methodName,params.parameterTypes,params.args);
	}
	
	@Override
	public Object runMethodInEjbContext(String className, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		// TODO Auto-generated method stub
		logger.info("run Test Method in EJB Context version 2");
		try (TxHandler txHandler = new TxHandler(this.tx, true)) {
			Class<?> clazz = Class.forName(className);
			Object instance = clazz.newInstance();
			
			Method method = clazz.getMethod(methodName, parameterTypes);
			Object res = method.invoke(instance, args);
			
			txHandler.commit();
			return res;
		}
	}

}
