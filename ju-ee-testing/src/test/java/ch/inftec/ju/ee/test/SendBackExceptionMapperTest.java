package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SendBackExceptionMapperTest {
	@Test
	public void givenException_convertStacktraceToString_createsExceptionStacktrace() {
		String stacktrace = createHelloWorldExceptionStacktrace();
		
		assertThat(stacktrace.startsWith("java.lang.RuntimeException: Hello World"), is(true));
	}
	
	private String createHelloWorldExceptionStacktrace() {
		RuntimeException ex = new RuntimeException("Hello World");
		String stacktrace = SendBackExceptionMapper.convertStacktraceToString(ex);
		
		return stacktrace;
	}
	
	@Test
	public void givenExceptionOnClasspath_reconstructOriginalException_createsOriginalInstance() {
		String stacktrace = createHelloWorldExceptionStacktrace();
		
		// REST will return an exception as a java.lang.Exception
		Exception restException = new Exception(stacktrace);
		
		RuntimeException originalException = (RuntimeException) SendBackExceptionMapper.reconstructOriginalException(restException);
		
		assertThat(originalException instanceof RuntimeException, is(true));
	}
	
	@Test
	public void givenUnknownException_reconstructOriginalException_returnsGivenException() {
		String stacktrace = "foo.bar.Exception: Hello";
		
		// REST will return an exception as a java.lang.Exception
		Exception restException = new Exception(stacktrace);
		
		Exception originalException = (Exception) SendBackExceptionMapper.reconstructOriginalException(restException);
		
		assertThat(originalException, is(sameInstance(restException)));
	}
}
