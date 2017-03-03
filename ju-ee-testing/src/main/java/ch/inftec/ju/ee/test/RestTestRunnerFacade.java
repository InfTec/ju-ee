package ch.inftec.ju.ee.test;

import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterDeserializer;
import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterSerializer;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.PropertyChain;
import ch.inftec.ju.util.SystemPropertyTempSetter;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.nio.charset.StandardCharsets.*;

public class RestTestRunnerFacade implements TestRunnerFacade{
	private Logger logger = LoggerFactory.getLogger(RestTestRunnerFacade.class);
	//private static Logger logger = Logger.getLogger(RestTestRunnerFacade.class);
	private final String RESOURCE_APP_PATH;
	private ObjectMapper objectMapper;
	
	public RestTestRunnerFacade(ObjectMapper objectMapper, String restUrl){
		RESOURCE_APP_PATH = restUrl;
		this.objectMapper=objectMapper;
	}
	
	
	public RestTestRunnerFacade(){

		RESOURCE_APP_PATH = getUrlStringFromProperties();

		//configure Object Mapper to use custom De/Serilalizer
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);

		SimpleModule module = new SimpleModule();
		module.addDeserializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterDeserializer());
		objectMapper.registerModule(module);

		module = new SimpleModule();
		module.addSerializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterSerializer());
		objectMapper.registerModule(module);
		
		/*
		SimpleModule module = new SimpleModule();
		module.addDeserializer((Class<Map<String,String>>)(Class)Map.class, new MapDeserializer());
		objectMapper.registerModule(module);
		
		module = new SimpleModule();
		module.addSerializer((Class<Map<String,String>>)(Class)Map.class, new MapSerializer());
		objectMapper.registerModule(module);
		*/
		/*
		module = new SimpleModule();
		module.addDeserializer(TestRunnerAnnotationHandler.class, new TestRunnerAnnotationHandlerDeserializer());
		objectMapper.registerModule(module);
		*/
	}
	
	private String getUrlStringFromProperties(){
		
		 // "http://localhost:8080/servicedb/rest/testRunner/";

		PropertyChain pc = JuUtils.getJuPropertyChain();
		
		Integer port = pc.get("ju-util-ee.rest.port", Integer.class, true);
		Integer portOffset = pc.get("ju-util-ee.portOffset", Integer.class, true);
		
		String restResourceLocator = String.format("http://%s:%d/%s/%s/",
				pc.get("ju-util-ee.rest.host", true),
				port+portOffset,
				pc.get("ju-util-ee.rest.contextBase", true),
				pc.get("ju-util-ee.rest.appName", true)
			);

		logger.debug("Run Container Test via Rest : " + restResourceLocator);
		
		return restResourceLocator;
	}
	
	@Override
	public String getVersion() {
		logger.debug("return version");
		
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
		logger.debug("[preTestActions] invoke Rest request");
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
		logger.debug("[runTestMethodInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);
	}

	@Override
	public void runPostTestActionsInEjbContext(TestRunnerAnnotationHandler handler) throws Exception {
		String jsonString = objectMapper.writeValueAsString(handler);
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "postTestActions");
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(jsonString, MediaType.APPLICATION_JSON));
		logger.debug("[runPostTestActionsInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);		
	}

	@Override
	public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter) {
		
		try{		
		String handlerJson = UTF8encode(objectMapper.writeValueAsString(handler)),
				tempSetterJson = UTF8encode(objectMapper.writeValueAsString(tempSetter));
		
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "cleanupTestRun").
				queryParam("handler",handlerJson).
				queryParam("tempSetter",tempSetterJson);
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildGet();
		logger.debug("[cleanupTestRun] invoke Rest request");
		Response response = preTestInvocation.invoke();
		handleResponse(response);
		
		}catch(Exception ex){
			logger.error("Cleanup of test run failed", ex);
		}
	}

	/*
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
		
		Class<?> clazz = Class.forName(className).getMethod(methodName,parameterTypes).getReturnType();
				
		return handleResponse(response,clazz);
	}
	*/

	//approach with Get Request
	@Override
	public Object runMethodInEjbContext(String className, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		
		Client client = ClientBuilder.newClient();
		WebTarget SimpleResource = client.target(RESOURCE_APP_PATH + "runMethod2").
				queryParam("className", UTF8encode(className)).
				queryParam("methodName",UTF8encode( methodName)).
				queryParam("parameterTypes", UTF8encode(objectMapper.writeValueAsString(parameterTypes))).
				queryParam("args", UTF8encode(objectMapper.writeValueAsString(args)));
		
		Invocation preTestInvocation = SimpleResource.request(MediaType.APPLICATION_JSON).buildGet();
		logger.debug("[runPostTestActionsInEjbContext] invoke Rest request");
		Response response = preTestInvocation.invoke();
		
		Class<?> clazz = Class.forName(className).getMethod(methodName,parameterTypes).getReturnType();
				
		return handleResponse(response,clazz);
	}
	
	// check response when no return value is expected
	private void handleResponse(Response response) throws Exception {
		
		int status = response.getStatus();
		if(status < 300 ){
			logger.debug("Response OK : " + status);
		}else{
			getExceptionFromResponse(response);
		}
	}
	
	// check the response and get return value from response body
	private <T> T handleResponse(Response response, Class<T> objClass) throws Exception {
		
		int status = response.getStatus();
		
		// Recover Return value from Response
		if(status < 300 ){
				logger.info("Response OK : " + status);
				
				/*
				if( ClassUtils.isPrimitiveOrWrapper(objClass))
					return response.readEntity(objClass);
				
				if( objClass == String.class)
					return (T) response.readEntity(String.class);
				 */
				
				
				// only use custom objectmapper if we expect our special helper classes
				if(	objClass == SystemPropertyTempSetter.class    ||
					objClass == TestRunnerAnnotationHandler.class
				){
					String jsonString =  response.readEntity(String.class);
					T obj = objectMapper.readValue(jsonString, objClass);
					return obj;
				}

				// Should be Ok if we deal with serializable Objects
				return response.readEntity(objClass);

		}
		
		getExceptionFromResponse(response);
		
		return null;
	}
	
	private void getExceptionFromResponse(Response response) throws Exception{
				
		// Try to find reason for error		
		int status = response.getStatus();
		
		logger.error("Request failed with error code: " + status);
		
		// reconstruct the exception
		if(status >= 500){
			logger.error("Internal Server error");
			
			// Retrieve the Stacktrace as String from the response body
			// The Stacktrace can be used to reconstruct the exception
			String exceptionAsString = response.readEntity(String.class);
			throw new Exception(exceptionAsString);
		} else {
			throw new Exception("Couldn't run remote test: " + getResponseContents(response));
		}
	}
	
	private String getResponseContents(Response response) {
		try {
			return response.readEntity(String.class);
		} catch (Exception ex) {
			return "Couldn't read response: " + ex.getMessage();
		}
		
	}
	
	private String UTF8encode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}
}
