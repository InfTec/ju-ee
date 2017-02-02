package ch.inftec.ju.ee.test.serialize;

import java.io.IOException;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.inftec.ju.ee.test.RunMethodRestParamEncapsulationObject;

public class RunMethodRestParamEncapsulationObjectDeserializer extends JsonDeserializer<RunMethodRestParamEncapsulationObject>{

	Logger logger = Logger.getLogger(RunMethodRestParamEncapsulationObjectDeserializer.class);
	
	@Override
	public RunMethodRestParamEncapsulationObject deserialize(JsonParser parser, DeserializationContext context) throws JsonProcessingException, IOException{
			
		
	    logger.info("Deserializing RunMethodRestParamEncapsulationObject");

		
		ObjectCodec oc = parser.getCodec();
	    JsonNode node = oc.readTree(parser);
	    
	    logger.info(node);
	    
		ObjectMapper objectMapper = new ObjectMapper();

				
		RunMethodRestParamEncapsulationObject encapsulation = new RunMethodRestParamEncapsulationObject();
		encapsulation.className = node.get("className").textValue();
		encapsulation.methodName = node.get("methodName").textValue();
		
		
		logger.info(node.get("parameterTypes"));
		logger.info(node.get("parameterTypes").toString());
		
		Class<?>[] parameterTypes =(Class<?>[])  objectMapper.readValue(node.get("parameterTypes").toString(),Class[].class);
		String[] argStrings = objectMapper.readValue(node.get("args").toString(),String[].class);
		Object[] args = new Object[parameterTypes.length];
		
		for(int i=0;i<parameterTypes.length;i++)
			args[i] = objectMapper.readValue(argStrings[i],parameterTypes[i]);
		
		
		encapsulation.parameterTypes = parameterTypes;
		encapsulation.args = args;
		
		return encapsulation;
	
	}

}
