package ch.inftec.ju.ee.test;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.inftec.ju.util.SystemPropertyTempSetter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper>{
	
	Logger logger = Logger.getLogger(JacksonObjectMapperProvider.class);
	
	private ObjectMapper objectMapper;
	
    public JacksonObjectMapperProvider() throws Exception {
        this.objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
	
		// add custom modules Serializers and Deserializers
		SimpleModule module = new SimpleModule();
		module.addSerializer((Class<Map<String,String>>)(Class)Map.class, new MapSerializer());
		objectMapper.registerModule(module);
		
		module = new SimpleModule();
		module.addDeserializer((Class<Map<String,String>>)(Class)Map.class, new MapDeserializer());
		objectMapper.registerModule(module);
		
		module = new SimpleModule();
		module.addDeserializer(TestRunnerAnnotationHandler.class, new TestRunnerAnnotationHandlerDeserializer());
		objectMapper.registerModule(module);
    }
    

    public ObjectMapper getContext(Class<?> objectType) {
    	
    	logger.info("Return Custom Objectmapper for class : "+ objectType.getName());
        return objectMapper;
    }

}