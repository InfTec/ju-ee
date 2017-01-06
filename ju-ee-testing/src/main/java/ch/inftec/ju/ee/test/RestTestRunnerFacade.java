package ch.inftec.ju.ee.test;

import ch.inftec.ju.util.SystemPropertyTempSetter;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
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
		
		try{

		String jsonString = objectMapper.writeValueAsString(handler);		
		logger.info(jsonString);

		Client client = ClientBuilder.newClient();
		//ResteasyClient client = new ResteasyClientBuilder().build();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "preTestActions");
		
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		
		logger.info("[preTestActions] invoke Rest request");
		Response response = preTestInvocation.invoke();

		int status = response.getStatus();
		
		// Recover Return value from Response
		if(status < 300){
				logger.info("[preTestActions] OK Response :" + status);
				
				jsonString = response.readEntity(String.class);
				logger.info(jsonString);
				SystemPropertyTempSetter tempsetter = objectMapper.readValue(jsonString, SystemPropertyTempSetter.class);
				return tempsetter;
		}else{
			logger.info("[preTestActions] Response Code : " + status);
		}
		
		// Try to find reason for error
		
		if(status >= 400 && status < 500){
				logger.info("[preTestActions] Service not found");
		}	
		
		
		// reconstruct the exception
		if(status >= 500){
			logger.info("[preTestActions] Internal Server error");
			Object ex = null;
			try{
				ex =  response.readEntity(Exception.class);
			}catch(Exception exception){
				logger.error("Unable to deserialize Response body");
				throw exception;
			}
			if(ex instanceof String){
				logger.error(ex);
				throw new InternalServerErrorException((String) ex);
			}
			if(ex instanceof Exception ){
				throw (Exception) ex;
			}
		}

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void runTestMethodInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object runMethodInEjbContext(String className, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
