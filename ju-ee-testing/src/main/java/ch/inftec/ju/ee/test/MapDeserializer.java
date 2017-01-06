package ch.inftec.ju.ee.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MapDeserializer extends JsonDeserializer<Map<String,String>>{
	
	Logger logger = Logger.getLogger(MapDeserializer.class);

	@Override
	public Map<String,String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JsonProcessingException, IOException  {
		
		HashMap<String,String> map = new HashMap<>();
		
		logger.info("Invoking Deserialization on HashMap Instance");
		
		ObjectCodec oc = jsonParser.getCodec();
	    JsonNode node = oc.readTree(jsonParser);
	    
	    Iterator<String> it = node.fieldNames();
	    while(it.hasNext()){
	    	String key = it.next();
	    	map.put(key, node.get(key).textValue());
	    	logger.info(key + " : " + node.get(key).textValue());
	    }
	    
	    return map;
	}

}
