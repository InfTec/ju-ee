package ch.inftec.ju.ee.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import ch.inftec.ju.util.ReflectUtils;

/**
 *  * @author stefan.andonie@inftec.ch
 *
 *	Register a Exception Mapper to catch all Exceptions that might occur in a Container Tests
 *	Since Exception Serialisazion is a bit tricky we send back the Stacktrace as String
 *  It is possible to reconstruct the original Exception later on the client Side
 *  
 */
@Provider
public class SendBackExceptionMapper implements ExceptionMapper<TestRunnerFacadeWrappingException>{
	private static Logger logger = Logger.getLogger(SendBackExceptionMapper.class);
	
	@Override
	public Response toResponse(TestRunnerFacadeWrappingException wrappingException) {
		Throwable originalThrowable = wrappingException.getCause();
		
		// try to get rid of overhead and get the original cause
		if (originalThrowable instanceof InvocationTargetException) {
			originalThrowable = originalThrowable.getCause();
		}
		
		if (originalThrowable instanceof EJBTransactionRolledbackException) {
			originalThrowable = originalThrowable.getCause();
		}
		
		logger.info("Exception Intercepted : "+originalThrowable.getClass().getName() +" : "+originalThrowable.getMessage());
		
		// factoring a HTTP Error Response (500) and pack the stacktrace in the body
		return Response
				.serverError()
				.entity(convertStacktraceToString(originalThrowable))
				.type(MediaType.TEXT_PLAIN)
				.build();
	}
	
	// Simple way to get the stacktrace as String
	static String convertStacktraceToString(Throwable t){
        StringWriter stringWritter = new StringWriter();
        PrintWriter printWritter = new PrintWriter(stringWritter, true);
        t.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush(); 
        return stringWritter.toString();
	}
	
	static Throwable reconstructOriginalException(Exception restException) {
		final String messageDelimiter = ": ";
		
		String stacktrace = restException.getMessage();
		
		// Stacktraces will start with the exception name, followed by a ": " and the message (including the stack trace), e.g.:
		// java.lang.RuntimeException: "Hello World"...
		
		int posColon = stacktrace.indexOf(messageDelimiter);
		
		if (posColon < 1) {
			return restException;
		} else {
			String originalExceptionClassName = stacktrace.substring(0, posColon);
			String message = stacktrace.substring(posColon + messageDelimiter.length());
			
			Throwable originalException = reconstructOriginalException(originalExceptionClassName, message);
			
			return originalException == null 
					? restException
					: originalException;
		}
	}
	
	private static Throwable reconstructOriginalException(String className, String message) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) Class.forName(className);
			
			Throwable originalException = ReflectUtils.newInstance(exceptionClass, true, message);
			return originalException;
		} catch (Exception ex) {
			logger.warn(String.format("Couldn't reconstruct original exception for %s: %s", className, ex.getMessage()));
			return null;
		}
	}
}
