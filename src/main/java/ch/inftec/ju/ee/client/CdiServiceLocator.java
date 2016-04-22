package ch.inftec.ju.ee.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.helper.FindHelper;
import ch.inftec.ju.util.helper.FindHelperBuilder;

/**
 * ServiceLocator that looks up CDI beans programmatically using
 * the BeanManager interface of a Container.
 * @author Martin
 *
 */
public interface CdiServiceLocator {
	/**
	 * Default CDI lookup of the specified type. Aims to return the same instance as @Inject would inject into a field.
	 * 
	 * @param clazz
	 *            Desired type to get from CDI
	 * @return Instance of T
	 * @throws JuRuntimeException
	 *             if we don't find exactly one matching instance
	 */
	public <T> T cdi(Class<T> clazz);
	
	/**
	 * Looks up a resource in the CDI BeanManager. Aims to return the same intance as @Inject with qualifiers would
	 * inject into a field.
	 * 
	 * @param clazz
	 *            Type of the resource
	 * @param annotations
	 *            Annotation qualifiers for the Beans to be looked up
	 * @return CDI bean or null if none was found. If multiple beans are found without a Default bean,
	 *         an exception is rised.
	 */
	public <T> T cdiAnno(Class<T> clazz, Annotation... annotations);

	/**
	 * Gets a list of all CDI beans for the specified type. This will also include @Alternative beans.
	 * 
	 * @param clazz
	 *            Type of the bean
	 * @return List containing all retrieved beans. If none was found, the list will be empty.
	 */
	public <T> List<T> cdiAll(Class<T> clazz);

	/**
	 * Gets a list of all CDI beans for the specified type.
	 * 
	 * @param clazz
	 *            Type of the bean
	 * @param annotations
	 *            List of annotation qualifiers the bean should have. For @Any, the
	 *            public field ServiceLocator.ANY can be used.
	 * @return List containing all retrieved beans. If none was found, the list will be empty.
	 */
	public <T> List<T> cdiAllAnno(Class<T> clazz, Annotation... annotations);

	/**
	 * Get a @Named annotated object with the specified name.
	 * @param clazz Desired type to get from CDI
	 * @param name Value of the @Named annotation
	 * @return Instance of T
	 * @throws JuRuntimeException if we don't find exactly one matching instance
	 */
	public <T> T cdiNamed(Class<T> clazz, String name);
	
	/**
	 * Returns a builder to issue complex CDI queries.
	 * @param clazz Desired type to get from CDI
	 * @return Builder to construct complex CDI queries
	 */
	public <T> CdiComplexLookupBuilder<T> cdiComplex(Class<T> clazz);

	/**
	 * Helper to construct complex CDI queries
	 * 
	 * @author Martin Meyer <martin.meyer@inftec.ch>
	 *
	 * @param <T> Desired type to get from CDI
	 */
	public static final class CdiComplexLookupBuilder<T> {
		private final Class<T> expectedType;
		private final BeanManager bm;
		
		private ArrayList<Annotation> annotations = new ArrayList<>();
		
		CdiComplexLookupBuilder(Class<T> expectedType, BeanManager bm) {
			this.expectedType = expectedType;
			this.bm = bm;
		}
		
		/**
		 * Bean is annotated with @Named annotation
		 * @param name Value of the @Named annotation
		 * @return
		 */
		public CdiComplexLookupBuilder<T> named(String name) {
			this.annotations.add(ServiceLocatorUtils.createNamedAnnotation(name));
			
			return this;
		}
		
		/**
		 * Bean is annotated with @ScopeControl annotation
		 * @return
		 */
		public CdiComplexLookupBuilder<T> scopeControl() {
			this.annotations.add(ServiceLocatorUtils.createScopeControlAnnotation());
			
			return this;
		}
		
		/**
		 * Returns a FindHelper instance to query the result set of CDI objects
		 * found by our settings.
		 * @return FindHelper instance
		 */
		public FindHelper<T> find() {
			Set<Bean<?>> beans = this.bm.getBeans(this.expectedType, this.annotations.toArray(new Annotation[0]));
			List<T> instances = ServiceLocatorUtils.toInstances(this.bm, beans, this.expectedType, null);
			
			return new FindHelperBuilder<T>()
				.collection(instances)
				.createFindHelper();
		}
	}
}
