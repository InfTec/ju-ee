package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JsonParameterHandlerTest {
	JsonParameterSerializer serializer = new JsonParameterSerializer();
	
	@Test
	public void givenNullParameterOfLong_parameterCanBeParsed() {
		String parameterTypes = getParameterTypesJson(Long.class);
		String argJson = toJson(new Object[] {null});
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0], is(nullValue()));
	}
	
	private String getParameterTypesJson(Class<?>... types) {
		return serializer.toJsonString(types);
	}
	
	private String toJson(Object...objs) {
		return serializer.toJsonString(objs);
	}
	
	@Test
	public void givenSingleLongParameter_canBeParsed() {
		String parameterTypes = getParameterTypesJson(Long.class);
		String argJson = toJson(1L);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof Long, is(true));
		assertThat((Long) values[0], is(1L));
	}
	
	@Test
	public void givenDoubleLongParameter_canBeParsed() {
		String parameterTypes = getParameterTypesJson(Long.class, Long.class);
		String argJson = toJson(1L, 2L);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(2));
		assertThat(values[0] instanceof Long, is(true));
		assertThat(values[1] instanceof Long, is(true));
		assertThat((Long) values[0], is(1L));
		assertThat((Long) values[1], is(2L));
	}
	
	@Test
	public void givenMixedTypeParameter_canBeParsed() {
		String parameterTypes = getParameterTypesJson(Long.class, String.class);
		String argJson = toJson(1L, "Hello");
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(2));
		assertThat(values[0] instanceof Long, is(true));
		assertThat(values[1] instanceof String, is(true));
		assertThat((Long) values[0], is(1L));
		assertThat((String) values[1], is("Hello"));
	}
	
	@Test
	public void givenNullArgs_canBeParsed() {
		JsonParameterHandler handler = new JsonParameterHandler(null, null);
		
		Object[] values = handler.getParameterValues();
		assertThat(values.length, is(0));
	}
	
	@Test
	public void givenEmptyArgs_canBeParsed() {
		JsonParameterHandler handler = new JsonParameterHandler("", "");
		
		Object[] values = handler.getParameterValues();
		assertThat(values.length, is(0));
	}
}
