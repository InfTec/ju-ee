package ch.inftec.ju.ee.test;

import ch.inftec.ju.util.SystemPropertyTempSetter;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class RestTestRunnerFacade implements TestRunnerFacade{

	private Logger logger = Logger.getLogger(RestTestRunnerFacade.class);
	private static final String RESOURCE_APP_PATH = "http://localhost:18080/ju-ee-ear-web/rest/testRunner/";
	
	private ObjectMapper objectMapper;
	
	public RestTestRunnerFacade(){
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		
		SimpleModule module = new SimpleModule();
		module.addDeserializer((Class<Map<String,String>>)(Class)Map.class, new MapDeserializer());
		objectMapper.registerModule(module);

	}
	
	@Override
	public String getVersion() {
		// https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/Invocation.Builder.html
		logger.info("return version");
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		WebTarget versionResource = client.target(RESOURCE_APP_PATH + "version");
		Invocation versionInvocation = versionResource.request().buildGet();
		String version = versionInvocation.invoke(String.class);
		logger.info("version is : " + version);
		return version;
	}

	@Override
	public SystemPropertyTempSetter runPreTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		
		String jsonString = objectMapper.writeValueAsString(handler);		
		Client client = ClientBuilder.newClient();
		//ResteasyClient client = new ResteasyClientBuilder().build();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "preTestActions");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.info("[preTestActions] invoke Rest request");
		Response response = preTestInvocation.invoke();
		SystemPropertyTempSetter tempSetter = handleResponse(response, SystemPropertyTempSetter.class);
		return tempSetter;
	}

	@Override
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		
		String jsonString = objectMapper.writeValueAsString(handler);
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "runMethodinEjb");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.info("[runTestMethodInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);
	}

	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		String jsonString = objectMapper.writeValueAsString(handler);
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "postTestActions");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.info("[runPostTestActionsInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);		
	}

	@Override
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		
		try{
		CleanupRestParamEncapsulationObject params = new CleanupRestParamEncapsulationObject();
		params.handler = handler;
		params.tempSetter = tempSetter;
		String jsonString = objectMapper.writeValueAsString(params);
		logger.info(jsonString);
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "cleanupTestRun");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.info("[cleanupTestRun] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);
		
		}catch(Exception exception){
			logger.error(exception);
		}
	}

	@Override
	public Object runMethodInEjbContext(String className, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {

		RunMethodRestParamEncapsulationObject params = new RunMethodRestParamEncapsulationObject();
		params.className = className;
		params.methodName = methodName;
		params.parameterTypes = parameterTypes;
		params.args = args;
		String jsonString = objectMapper.writeValueAsString(params);
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "runMethod2");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.info("[runPostTestActionsInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		return handleResponse(response,Object.class);
	}
	
	private void handleResponse(Response response) throws Exception {
		
		int status = response.getStatus();
		if(status < 300 ){
			logger.info("Response OK : " + status);
		}else{
			getExceptionFromResponse(response);
		}
	}
	
	private <T> T handleResponse(Response response, Class<T> objClass) throws Exception {
		
		int status = response.getStatus();
		
		// Recover Return value from Response
		if(status < 300 ){
				logger.info("Response OK : " + status);
				
				String jsonString = response.readEntity(String.class);
				logger.info(jsonString);
				T obj = objectMapper.readValue(jsonString, objClass);
				return obj;
		}
		
		getExceptionFromResponse(response);
		
		return null;
	}
	
	private void getExceptionFromResponse(Response response) throws Exception{
				
		// Try to find reason for error
		//if(status >= 400 && status < 500) logger.info("[preTestActions] Service not found");
		
		int status = response.getStatus();
		logger.info("Response Code : " + status);

		// reconstruct the exception
		if(status >= 500){
			logger.info("Internal Server error");
			
			Object ex = null;
			try{
				//ex =  response.readEntity(Exception.class);
				
				logger.error(response.readEntity(String.class));
			}catch(Exception exception){
				logger.error("Unable to deserialize Response body");
				throw exception;
			}
			
			/*
			if(ex instanceof String){
				logger.error((String) ex);
				throw new InternalServerErrorException((String) ex);
			}
			if(ex instanceof Exception ){
				throw (Exception) ex;
			}
			*/
		}
	}
}
