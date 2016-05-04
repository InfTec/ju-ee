package ch.inftec.ju.ee.test;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBException;

import org.junit.ComparisonFailure;

/**
 * Remote call related utility functions.
 * @author martin.meyer@inftec.ch
 *
 */
public class RemoteUtils {
	/**
	 * Gets the actual throwable from a throwable returned by an EJB client call. The actual
	 * throwable might be wrapped up in some other EJB generic exceptions.
	 * @param t Throwable received
	 * @return Actual throwable
	 */
	public static Throwable getActualThrowable(Throwable t) {
		if (t instanceof InvocationTargetException) {
			return t.getCause();
		} else if (t instanceof EJBException) {
			// DataVerifier contain a nested RuntimeException more
			if (t.getCause() != null && t.getCause().getClass() == RuntimeException.class) {
				return t.getCause().getCause();
			} else {
				return t.getCause();
			}
		} else if (t instanceof ExceptionInInitializerError) {
			throw new IllegalStateException("Looks like we couldn't connect to JBoss. Make sure it is running.", t);
		} else if (t.getCause() instanceof ComparisonFailure) {
			return t.getCause();
		} else {
			return t;
		}		
	}
}
