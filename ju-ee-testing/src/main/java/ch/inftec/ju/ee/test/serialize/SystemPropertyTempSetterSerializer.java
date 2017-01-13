package ch.inftec.ju.ee.test.serialize;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.inftec.ju.util.SystemPropertyTempSetter;

public class SystemPropertyTempSetterSerializer extends JsonSerializer<SystemPropertyTempSetter> {

	//Logger logger = Logger.getLogger(SystemPropertyTempSetterSerializer.class);
	
	@Override
	public void serialize(SystemPropertyTempSetter tempSetter, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		SimpleModule module = new SimpleModule();
		module.addSerializer((Class<Map<String,String>>)(Class)Map.class, new MapSerializer());
		objectMapper.registerModule(module);
		String jsonString = objectMapper.writeValueAsString(tempSetter);
		
		generator.writeString(jsonString);
		
	}
	
	
}
