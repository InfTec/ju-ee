package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private enum MyEnum {
		VALUE1,
		VALUE2;
	}
	
	@Test
	public void customEnum_canBeParsed() {
		String parameterTypes = getParameterTypesJson(MyEnum.class);
		String argJson = toJson(MyEnum.VALUE1);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof MyEnum, is(true));
		assertThat((MyEnum) values[0], is(MyEnum.VALUE1));
	}
	
	private static class MyType {
		private Map<String, Long> map = new HashMap<>();
		private String val;
	}
	
	@Test
	public void customType_canBeParsed() {
		String parameterTypes = getParameterTypesJson(MyType.class);
		
		MyType myType = new MyType();
		myType.map.put("key", 0L);
		myType.val = "hello";
		String argJson = toJson(myType);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof MyType, is(true));
		
		MyType readType = (MyType) values[0];
		assertThat(readType.map.size(), is(1));
		assertThat(readType.map.get("key"), is(0L));
		assertThat(readType.val, is("hello"));
	}
	
	@Test
	public void list_canBeParsed() {
		String parameterTypes = getParameterTypesJson(List.class);
		
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		
		String argJson = toJson(list);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof List, is(true));
		
		@SuppressWarnings("unchecked")
		List<Long> readList = (List<Long>) values[0];
		assertThat(readList.size(), is(2));
		assertThat(readList.get(0), is(1L));
		assertThat(readList.get(1), is(2L));
	}
	
	@Test
	public void emptyList_canBeParsed() {
		String parameterTypes = getParameterTypesJson(List.class);
		
		List<Long> list = new ArrayList<>();
		String argJson = toJson(list);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof List, is(true));
		
		@SuppressWarnings("unchecked")
		List<Long> readList = (List<Long>) values[0];
		assertThat(readList.size(), is(0));
	}
	
	@Test
	public void date_canBeParsed() {
		String parameterTypes = getParameterTypesJson(Date.class);
		Date date = new Date();
		String argJson = toJson(date);
		
		JsonParameterHandler handler = new JsonParameterHandler(parameterTypes, argJson);
		
		Object[] values = handler.getParameterValues();
		
		assertThat(values.length, is(1));
		assertThat(values[0] instanceof Date, is(true));
		
		Date readDate = (Date) values[0];
		assertThat(readDate, is(equalTo(date)));
	}
}
