package ch.inftec.ju.ee.test;

import ch.inftec.ju.util.SystemPropertyTempSetter;

/**
 * 
 * @author stefan.andonie@inftec.com
 *
 * Encapsulate all parameters of TestRunnerFacade method
 * 		public Object runMethodInEjbContext(String className, String methodName, Class&lt;?&gt;[] parameterTypes, Object[] args)
 * 
 * in a object since Rest allows only one Json object in the response body
 * 
 */
public class RunMethodRestParamEncapsulationObject {
	
	public String className, methodName;
	public Class<?>[] parameterTypes;
	public Object[] args;
	
	public RunMethodRestParamEncapsulationObject(){}
}