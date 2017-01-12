package ch.inftec.ju.ee.test;

/**
 * Helper exception used by the TestRunnerFacadeResource to wrap any exceptions that might
 * occurr. This is necessary to be able to define a specific ExceptionHandler to serialize
 * the exception for the REST response without having any side effects on other REST services.
 * 
 * @author stefan.andonie@inftec.com
 *
 */
class TestRunnerFacadeWrappingException extends RuntimeException {
	public TestRunnerFacadeWrappingException(Throwable cause) {
		super(cause);
	}
}
