package ch.inftec.ju.ee.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class SendBackExceptionMapper implements ExceptionMapper<Exception>{

	Logger logger = Logger.getLogger(SendBackExceptionMapper.class);
	
	@Override
	public Response toResponse(Exception exception) {
		logger.info("Exception Intercepted");
		logger.info(convertStacktraceToString(exception));
		return Response.ok(convertStacktraceToString(exception),MediaType.TEXT_PLAIN).status(Response.Status.INTERNAL_SERVER_ERROR).build();
		//return Response.ok(exception,MediaType.APPLICATION_JSON).status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private String convertStacktraceToString(Exception exception){
        StringWriter stringWritter = new StringWriter();
        PrintWriter printWritter = new PrintWriter(stringWritter, true);
        exception.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush(); 
        return stringWritter.toString();
	}

}
