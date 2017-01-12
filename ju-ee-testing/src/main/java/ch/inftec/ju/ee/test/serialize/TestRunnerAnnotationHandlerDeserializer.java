package ch.inftec.ju.ee.test.serialize;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jboss.logging.Logger;
import org.junit.runner.Description;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.inftec.ju.ee.test.TestRunnerAnnotationHandler;
import ch.inftec.ju.ee.test.TestRunnerFacade;
import ch.inftec.ju.ee.test.TestRunnerFacade.TestRunnerContext;

/**
 * 
 * @author stefan.andonie@inftec.com
 *
 */
public class TestRunnerAnnotationHandlerDeserializer extends JsonDeserializer<TestRunnerAnnotationHandler>{
	
	Logger logger = Logger.getLogger(TestRunnerAnnotationHandlerDeserializer.class);

	@Override
	public TestRunnerAnnotationHandler deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JsonProcessingException, IOException  {
		
		try {
		logger.info("Invoking Deserialization of TestRunnerAnnotationHandler Instance");
		
		ObjectCodec oc = jsonParser.getCodec();
	    JsonNode node = oc.readTree(jsonParser);
	    
	    JsonNode contextNode = node.get("context");

	    String testClassName = node.get("testClassName").textValue(),
	    	testMethodName 	= node.get("testMethodName").textValue();
	    
	    // construct TestRunnerContext;
	    TestRunnerContext context = new TestRunnerContext();	    
	    ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		context = mapper.readValue(contextNode.toString(), TestRunnerContext.class);

	    //new Method for constructing DbTestAnnotationHandler
	    Class clazz = TestRunnerAnnotationHandlerDeserializer.class.getClassLoader().loadClass(testClassName);
	    Method methode = clazz.getMethod(testMethodName);
	    Description description = Description.createTestDescription(clazz,testMethodName);

	    // now construct the Handler
	    TestRunnerAnnotationHandler handler = new TestRunnerAnnotationHandler(methode,description,context);
	    
	    return handler;
	    

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			logger.info("exception");
			logger.info(e);
			throw e;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.info("exception");
			logger.info(e);

		} catch (NoSuchMethodException e) {
			logger.info("exception");
			logger.info(e);
		}
		
		return null;
	}

}
