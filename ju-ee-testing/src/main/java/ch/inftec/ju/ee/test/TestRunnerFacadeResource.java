package ch.inftec.ju.ee.test;

import java.lang.reflect.Method;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.ClassUtils;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.inftec.ju.db.JuEmUtil;
import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler.ContainerTestContextSetter;
import ch.inftec.ju.ee.test.provider.ContainerTestContextResolver;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import javax.ejb.TransactionManagementType;

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
	/*
	@Path("cleanupTestRun")
	@POST
	@Consumes("application/json")
	public void cleanupTestRun(CleanupRestParamEncapsulationObject params) {
		cleanupTestRun(params.handler,params.tempSetter);
	}
	*/

	@Path("cleanupTestRun")
	@GET
	@Consumes("application/json")
	public void cleanupTestRun(@QueryParam("handler") String handlerJson,@QueryParam("tempSetter") String tempSetterJson) throws Exception {
		ObjectMapper objectMapper = new ContainerTestContextResolver().getContext(TestRunnerAnnotationHandler.class);
		TestRunnerAnnotationHandler handler = objectMapper.readValue(handlerJson,TestRunnerAnnotationHandler.class);
		SystemPropertyTempSetter tempSetter = objectMapper.readValue(tempSetterJson,SystemPropertyTempSetter.class);
		cleanupTestRun(handler,tempSetter);
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
	/*
	@Path("runMethod2")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	*/
	public Object runMethodInEjbContext(RunMethodRestParamEncapsulationObject params) throws Exception {
		
		logger.info(params.className);
		logger.info(params.methodName);
		logger.info(params.parameterTypes);
		
		for(int i=0;i < params.args.length;i++){
			
			Class<?> parameterClass = params.parameterTypes[i],
					argClass = params.args[i].getClass();
						
			if(
					parameterClass != argClass
					&& params.args[i] instanceof Number
					&& ClassUtils.isPrimitiveWrapper(argClass)
					&& ClassUtils.isPrimitiveWrapper(parameterClass)
			){
				logger.info("convert to WrapperType: "+params.parameterTypes[i].getName()+" <- "+params.args[i].getClass().getName()+" : "+params.args[i]);
				Number numerical = (Number) params.args[i];
				
				if	   (parameterClass == Byte.class)	params.args[i] = numerical.byteValue();
				else if(parameterClass == Short.class)	params.args[i] = numerical.shortValue();
				else if(parameterClass == Integer.class)params.args[i] = numerical.intValue();
				else if(parameterClass == Long.class)	params.args[i] = numerical.longValue();
				else if(parameterClass == Double.class) params.args[i] = numerical.doubleValue();
				//else if(parameterClass == Character.class) params.args[i] = (char) numerical.intValue();
			}
			
			/*
			 Possible to serialize -> deserialize as String[]
			 		for(int i=0;i<parameterTypes.length;i++)
					args[i] = objectMapper.readValue(argStrings[i],parameterTypes[i]);
			 */
			
			/*
			// Simple case where Long was deserialized as Integer
			if(params.parameterTypes[i] != params.args[i].getClass()){
				if(params.args[i].getClass() == Integer.class && params.parameterTypes[i] == Long.class){
					params.args[i] = new Long((Integer) params.args[i]);
				}
			}
			*/
			
			// boolean should not be a problem as long as serialized as true/false
			// char could be a problem char represation as String or Number
		}
		
		return runMethodInEjbContext(params.className,params.methodName,params.parameterTypes,params.args);
		
	}
	
	
	@Path("runMethod2")
	@GET
	@Produces("application/json")
	@Consumes("application/json")
	public Object runMethodInEjbContext(
			@QueryParam("className") String className,
			@QueryParam("methodName") String methodName,
			@QueryParam("parameterTypes") String parameterTypesJson, 
			@QueryParam("args") String argsJson
	) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		Class<?>[] parameterTypes = objectMapper.readValue(parameterTypesJson, Class[].class);
		String[] argString = objectMapper.readValue(argsJson, String[].class);
		
		int len = argString == null 
				? 0 
				: argString.length;
		Object[] args = new Object[len];
		for (int i = 0; i < len; i++) {
			args[i] = objectMapper.readValue(argString[i], parameterTypes[i]);
		}
		
		return runMethodInEjbContext(className,methodName,parameterTypes,args);
	}
	
	@Override
	public Object runMethodInEjbContext(String className,String methodName,Class<?>[] parameterTypes, Object[] args) throws Exception {

		// Simple case where Long was deserialized as Integer
		for(int i=0;i < args.length;i++)
		if(parameterTypes[i] != args[i].getClass()){
			if(args[i].getClass() == Integer.class && parameterTypes[i] == Long.class){
				args[i] = new Long((Integer) args[i]);
			}
		}
		
		logger.info("run Test Method in EJB Context version 2");
		try (TxHandler txHandler = new TxHandler(this.tx, true)) {
			Class<?> clazz = Class.forName(className);
			Object instance = clazz.newInstance();

			Method method = clazz.getMethod(methodName, parameterTypes);
			Object res = method.invoke(instance, args);			
			txHandler.commit();			
			return res;
		} catch (Throwable t) {

			throw wrapThrowable(t);
		}
	}
}
