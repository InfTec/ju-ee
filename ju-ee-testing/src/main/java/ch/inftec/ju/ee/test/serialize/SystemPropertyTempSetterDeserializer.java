package ch.inftec.ju.ee.test.serialize;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.inftec.ju.util.SystemPropertyTempSetter;

public class SystemPropertyTempSetterDeserializer extends JsonDeserializer<SystemPropertyTempSetter>{

	// Logger logger = Logger.getLogger(SystemPropertyTempSetterDeserializer.class);

	@Override
	public SystemPropertyTempSetter deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		
		
		ObjectCodec oc = parser.getCodec();
	    JsonNode node = oc.readTree(parser);
	    
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		SimpleModule module = new SimpleModule();
		module.addDeserializer((Class<Map<String,String>>)(Class)Map.class, new MapDeserializer());
		objectMapper.registerModule(module);
				
		return objectMapper.readValue(node.textValue(), SystemPropertyTempSetter.class);
	}

}
