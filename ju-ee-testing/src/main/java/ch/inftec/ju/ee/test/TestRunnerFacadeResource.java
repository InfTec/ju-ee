package ch.inftec.ju.ee.test;

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

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.inftec.ju.db.JuEmUtil;
import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler.ContainerTestContextSetter;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.RequestScoped;


//@Path("/testRunner")
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
		
		/*
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		String jsonString = mapper.writeValueAsString(handler);
		
		logger.info(jsonString);
		*/
		
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

			/* Test Map Serialization
			
			tempSetter.setProperty("foo", "bar");
			tempSetter.setProperty("21", "27");
			tempSetter.setProperty("key", "value");
			
			tempSetter.setEnv("aaaaaa", "bbbbbb");
			tempSetter.setEnv("env", "env");
			tempSetter.setEnv("44", "55");

			*/
			return tempSetter;
		}
	}

	@Path("runMethod")
	@POST
	@Consumes("application/json")
	@Override
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Path("postTestActions")
	@POST
	@Consumes("application/json")
	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Path("cleanupTestRun")
	@POST
	@Consumes("application/json")
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		// TODO Auto-generated method stub
		
	}

	@Path("runMethod2")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Object runMethodInEjbContext(String className, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
