package ch.inftec.ju.ee.test;

import java.net.URLEncoder;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterDeserializer;
import ch.inftec.ju.ee.test.serialize.SystemPropertyTempSetterSerializer;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.SystemPropertyTempSetter;

public class JsonParameterSerializer {
	private final ObjectMapper objectMapper;
	
	public JsonParameterSerializer() {
		objectMapper = createObjectMapper();
	}
	
	private ObjectMapper createObjectMapper() {
		//configure Object Mapper to use custom De/Serilalizer
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
		
		// Avoid failure on reading read-only properties on custom types
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule module = new SimpleModule();
		module.addDeserializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterDeserializer());
		objectMapper.registerModule(module);

		module = new SimpleModule();
		module.addSerializer(SystemPropertyTempSetter.class, new SystemPropertyTempSetterSerializer());
		objectMapper.registerModule(module);
		
		return objectMapper;
	}
	
	public String toJsonString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception ex) {
			throw new JuRuntimeException("Couldn't convert object to JSON String: " + obj, ex);
		}
	}
	
	public String toEncodedJsonString(Object obj) {
		return UTF8encode(toJsonString(obj));
	}
	
	private String UTF8encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception ex) {
			throw new JuRuntimeException("Couldn't encode String: " + str, ex);
		}
	}
	
	public <T> T toObject(String jsonString, Class<T> type) {
		try {
			return objectMapper.readValue(jsonString, type);
		} catch (Exception ex) {
			throw new JuRuntimeException(String.format("Couldn't convert JSON to object: %s [type: %s]", jsonString, type));
		}
	}
	
	public <T> T toObject(Map<?, ?> serializedObject, Class<T> type) {
		return objectMapper.convertValue(serializedObject, type);
	}
}
