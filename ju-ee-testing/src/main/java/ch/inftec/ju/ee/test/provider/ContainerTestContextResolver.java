package ch.inftec.ju.ee.test.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler;
import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterDeserializer;
import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterSerializer;
import ch.inftec.ju.ee.test.serialize.TestRunnerAnnotationHandlerDeserializer;
import ch.inftec.ju.util.SystemPropertyTempSetter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ContainerTestContextResolver implements ContextResolver<ObjectMapper>{
	
	Logger logger = Logger.getLogger(ContainerTestContextResolver.class);
	
	private ObjectMapper objectMapper;
	
    public ContainerTestContextResolver() throws Exception {
        this.objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
	
		// add custom modules for Serializers and Deserializers
		SimpleModule module = new SimpleModule();
		module.addSerializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterSerializer());
		objectMapper.registerModule(module);
		
		module = new SimpleModule();
		module.addDeserializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterDeserializer());
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