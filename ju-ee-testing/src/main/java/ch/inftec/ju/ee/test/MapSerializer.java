package ch.inftec.ju.ee.test;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ch.inftec.ju.util.SystemPropertyTempSetter;

public class MapSerializer extends JsonSerializer<Map<String,String>> {
	
	Logger logger = Logger.getLogger(MapSerializer.class);

	@Override
	public void serialize(Map<String,String> map, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		logger.info("Serialize Map<String, String>");
		
		generator.writeStartObject();
		for(Entry<String, String> entry : map.entrySet()){
			generator.writeFieldName(entry.getKey());
			generator.writeString(entry.getValue());
		}		
		generator.writeEndObject();
	}

}
