package ch.inftec.ju.ee.test;

import ch.inftec.ju.util.SystemPropertyTempSetter;


/**
 * 
 * @author stefan.andonie@inftec.com
 *
 * Encapsulate all parameters of TestRunnerFacade Method
 * 		public void cleanupTestRun(TestRunnerAnnotationHandler handler, SystemPropertyTempSetter tempSetter)
 * 
 * in a object since Rest allows only one Json object in the response body
 * 
 */
public class CleanupRestParamEncapsulationObject {
	
	public TestRunnerAnnotationHandler handler;
	public SystemPropertyTempSetter tempSetter;
	
	public CleanupRestParamEncapsulationObject(){}	
}
