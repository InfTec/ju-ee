package ch.inftec.ju.ee.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import ch.inftec.ju.ee.cdi.ScopeControl;
import ch.inftec.ju.util.JuRuntimeException;

/**
 * Utility class containing shared ServiceLocator related funcationality. 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
class ServiceLocatorUtils {
	/**
	 * Workaround to get an instance of @Any
	 * 
	 * @author Martin
	 *
	 */
	@SuppressWarnings("all")
	static class AnnotationAny implements Annotation, Any {
		@Override
		public Class<? extends Annotation> annotationType() {
			return Any.class;
		}
	}

	/**
	 * Creates a new instance of a @Named annotation with the specified value.
	 * @param value Value of the @Named annotation
	 * @return
	 */
	public static Named createNamedAnnotation(final String value) {
		return new Named() {
			@Override
			public String value() {
				return value;
			}
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Named.class;
			}
		};
	}

	/**
	 * Creates a new instance of a @ScopeControl annotation
	 * @return
	 */
	public static ScopeControl createScopeControlAnnotation() {
		return new ScopeControl() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return ScopeControl.class;
			}
		};
	}
	
	/**
	 * Helper method to convert a set of Beans to actual instance.
	 * @param bm BeanManager that was used to retrieve the Beans
	 * @param beans Set of beans to convert
	 * @param clazz Type to convert to
	 * @param count If not null, we'll check if we have exactly count results and throw a JuRuntimeException if not
	 * @return List of instances
	 */
	public static <T> List<T> toInstances(BeanManager bm, Set<Bean<?>> beans, Class<T> clazz, Integer count) {
		List<T> instances = new ArrayList<>();
		for (Bean<?> bean : beans) {
			instances.add(toInstance(bm, bean, clazz));
		}
		
		if (count != null) {
			if (instances.size() != count) {
				throw new JuRuntimeException("Expected exactly %d result(s) for CDI lookup of %s, but found %d", count, clazz, instances.size());
			}
		}
		
		return instances;
	}

	/**
	 * Helper method to convert a Bean to an actual instance.
	 * 
	 * @param bm
	 *            BeanManager that was used to retrieve the Bean
	 * @param bean
	 *            Bean to convert
	 * @param clazz
	 *            Type to convert to
	 * @return Instance
	 */
	public static <T> T toInstance(BeanManager bm, Bean<?> bean, Class<T> clazz) {
		if (bean == null) {
			return null;
		} else {
			CreationalContext<?> cont = bm.createCreationalContext(bean);

			@SuppressWarnings("unchecked")
			T t = (T) bm.getReference(bean, clazz, cont);

			return t;
		}
	}
}
