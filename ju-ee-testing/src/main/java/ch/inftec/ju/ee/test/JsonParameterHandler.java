package ch.inftec.ju.ee.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ch.inftec.ju.util.JuRuntimeException;

class JsonParameterHandler {
	private final String parameterTypesJson;
	private final String argsJson;
	
	private final JsonParameterSerializer jsonParameterSerializer = new JsonParameterSerializer();
	
	private final Class<?>[] parameterTypes;
	
	public JsonParameterHandler(String parameterTypesJson, String argsJson) {
		this.parameterTypesJson = parameterTypesJson;
		this.argsJson = argsJson;
		
		parameterTypes = parseParameterTypes();
	}
	
	private Class<?>[] parseParameterTypes() {
		if (StringUtils.isEmpty(parameterTypesJson)) {
			return new Class<?>[0];
		} else {
			try {
				return jsonParameterSerializer.toObject(parameterTypesJson, Class[].class);
			} catch (Exception ex) {
				throw new JuRuntimeException("Couldn't parse parameter types: " + parameterTypesJson, ex);
			}
		}
	}
	
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	public Object[] getParameterValues() {
		if (StringUtils.isEmpty(argsJson)) {
			return new Object[0];
		} else {
			Object[] obj = jsonParameterSerializer.toObject(argsJson, Object[].class);
			return allignWithTypes(obj);
		}
	}
	
	private Object[] allignWithTypes(Object[] args) {
		int length = args == null
				? 0
				: args.length;
		
		Object[] alligned = new Object[length];
		Class<?>[] expectedTypes = getParameterTypes();
		
		for (int i = 0; i < length; i++) {
			Object obj = args[i];
			alligned[i] = align(obj, expectedTypes[i]);
		}
		
		return alligned;
	}
	
	private Object align(Object arg, Class<?> expectedType) {
		if (arg == null) {
			return arg;
		} else {
			if (isDefaultType(expectedType)) {
				return arg;
			} else if (arg instanceof Integer && expectedType == Long.class) {
				return ((Integer) arg).longValue();
			} else if (arg instanceof String && Enum.class.isAssignableFrom(expectedType)) {
				return argAsEnum(expectedType, (String) arg);
			} else if (arg instanceof Map) {
				return asCustomType(expectedType, (Map<?, ?>) arg);
			} else if (arg instanceof List) {
				// For the moment, we only support List<Long>. For more complex requirements, we'd have to
				// use custom serializer/deserializer for lists
				return integerToLongList((List<?>) arg);
			} else if (arg instanceof Long && expectedType == Date.class) {
				return new Date((Long) arg);
			} else {
				return arg;
			}
		}
	}
	
	private boolean isDefaultType(Class<?> expectedType) {
		return expectedType == String.class || expectedType == Integer.class;
	}
	
	private Object argAsEnum(Class<?> expectedType, String arg) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Class<? extends Enum> enumType = (Class<? extends Enum>) expectedType;
		@SuppressWarnings("unchecked")
		Object enumVal = Enum.valueOf(enumType, (String) arg);
		
		return enumVal;
	}
	
	private Object asCustomType(Class<?> expectedType, Map<?, ?> serializedObject) {
		return jsonParameterSerializer.toObject(serializedObject, expectedType);
	}
	
	private List<?> integerToLongList(List<?> list) {
		if (list == null || list.size() == 0) {
			return list;
		} else {
			List<Object> longList = new ArrayList<>();
			
			for (Object listItem : list) {
				if (listItem instanceof Integer) {
					longList.add(((Integer) listItem).longValue());
				} else {
					longList.add(listItem);
				}
			}
			
			return longList;
		}
	}
}
