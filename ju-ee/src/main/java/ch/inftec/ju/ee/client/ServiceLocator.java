package ch.inftec.ju.ee.client;

import java.lang.annotation.Annotation;

import ch.inftec.ju.ee.client.ServiceLocatorUtils.AnnotationAny;

/**
 * ServiceLocator implementing both CdiServiceLocator and JndiServiceLocator interfaces.
 * <p>
 * Can be used to lookup JNDI and CDI objects running in a container.
 * @author Martin
 *
 */
public interface ServiceLocator extends CdiServiceLocator, JndiServiceLocator {
	/**
	 * Instance of the Any annotation to be used for cdiAllAnno.
	 */
	public static Annotation ANY = new AnnotationAny();
}
