package ch.inftec.ju.ee.test;


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
			if (arg instanceof Integer && expectedType == Long.class) {
				return ((Integer) arg).longValue();
			} else {
				return arg;
			}
		}
	}
}
