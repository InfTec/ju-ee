package ch.inftec.ju.ee.cdi;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.util.AssertUtil;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.ReflectUtils;

/**
 * Helper class to load CDI implementations dynamically at run time using
 * system properties to configure them.
 * <p>
 * To 'activate' DynamicCdiLoader in a CDI project, add a new class that extends from it in a beans enabled
 * module.
 * <p>
 * Implementations to be found by the DynamicCdiLoader must implement the tag interface DynamicCdi. They should
 * implement another interface for the Service they implement (so we can have different implementations at all).
 * <p>
 * They can optionally define an annotation DynamicCdiTag with a String value. If they do, this implementation
 * will be loaded if the tag is activated.
 * <p>
 * A tag can be activated either by specify a property with the full class name as its key and the tag as its
 * value or by specifying the default tag using the property <code>ju.ee.cdi.defaultDynamicCdiTag</code>
 * <p>
 * If no tag is specified, the default value '-' will be used which stands for the default implementation.
 * @author martin.meyer@inftec.ch
 * <p>
 * <b>Important:</b>Every dynamic implementation has to extend the interface DynamicCdi AND to specify a tag
 * DynamicCdiTag. We need the tag to avoid duplicate declaration issues with weld. When we use a producer to
 * lookup the actual implementation, it will not have a tag thus making the declaration unambiguous.
 *
 */
@ApplicationScoped
public class DynamicCdiLoader {
	private Logger logger = LoggerFactory.getLogger(DynamicCdiLoader.class);
	
	/**
	 * Have CDI inject all implementations that are eligable for dynamic config.
	 * <p>
	 * Note: This will not work if we have to create an instance manually using the constructor. In this case,
	 * we need to have it injected in a managed Bean and passed to the loader using it's non-default constructor.
	 */
	@Inject @Any
	private Instance<DynamicCdi> dynamicCdis;
	
	public DynamicCdiLoader() {
		// Default constructor needed by Weld
	}
	
	/**
	 * A Producer of DynamicCdiLoader will need to get Instance&lt;DynamicCdi&gt; injected
	 * by Weld using the annotations <code>@Inject @Any</code>
	 * 
	 * @param dynamicCdis
	 */
	public DynamicCdiLoader(Instance<DynamicCdi> dynamicCdis) {
		this.dynamicCdis = dynamicCdis;
	}
	
	/**
	 * Gets the specified implementation for the given class.
	 * <p>
	 * This method is mainly public so we can test it from the esw-test1 package. Normally, it
	 * will not be called from outside.
	 * 
	 * @param clazz
	 *            Clazz to get implementation for (i.e. CDI instance with the appropriate SimulatorTag)
	 * @return Instance of clazz
	 * @throws JuRuntimeException
	 *             If no implementation can be found
	 */
	public <T> T getImplementation(Class<T> clazz) {
		AssertUtil.assertNotNull("Dynamic CDIs must be set or injected", this.dynamicCdis);
		
		// Get the tag for the specified class
		String tagName = JuUtils.getJuPropertyChain().get(clazz.getName(), false);
		
		String defaultTagName = JuUtils.getJuPropertyChain().get("ju.ee.cdi.defaultDynamicCdiTag", "-");
		
		logger.debug("Looking for implementation of class {} (tag={}, defaultTag={})", clazz, tagName, defaultTagName);
		
		Map<String, TypeCreator<T>> implementations = new HashMap<>();
		
		for (DynamicCdi simulatable : dynamicCdis) {
			// Handle factories
			if (DynamicCdiFactory.class.isAssignableFrom(simulatable.getClass())) {
				for (Method method : ReflectUtils.getDeclaredMethodsByAnnotation(simulatable.getClass(), DynamicCdiTag.class)) {
					if (method.getReturnType() == null || method.getParameterTypes().length > 0) {
						logger.warn("Illegal DynamicCdiFactory method. Must return a type and not have arguments: " + method);
					} else {
						if (clazz.isAssignableFrom(method.getReturnType())) {
							// Found match
							final DynamicCdi factory = simulatable;
							final Method factoryMethod = method;
							
							this.addType(clazz
									, implementations
									, method.getAnnotation(DynamicCdiTag.class)
									, new TypeCreator<T>() {
										@Override
										public T createType() {
											try {
												@SuppressWarnings("unchecked")
												T t = (T) factoryMethod.invoke(factory, (Object[])null);
												return t;
											} catch (Exception ex) {
												throw new JuRuntimeException("Couldn't invoke method " + factoryMethod, ex);
											}
										}
										@Override
										public String toString() {
											return factoryMethod.toString();
										}
									});
						}
					}
				}
			} else {
				if (clazz.isAssignableFrom(simulatable.getClass())) {
					@SuppressWarnings("unchecked")
					final T type = (T)simulatable;
					
					this.addType(clazz
							, implementations
							, type.getClass().getAnnotation(DynamicCdiTag.class)
							, new TypeCreator<T>() {
								@Override
								public T createType() {
									return type;
								}
								@Override
								public String toString() {
									return type.getClass().getName();
								}
							});
				}
			}
		}
		
		if (implementations.containsKey(tagName)) {
			logger.debug("Returning intance by tag match: {}", implementations.get(tagName));
			return implementations.get(tagName).createType();
		} else if (implementations.containsKey(defaultTagName)) {
			logger.debug("Returning default instance: {}", implementations.get(defaultTagName));
			return implementations.get(defaultTagName).createType();
		} else {
			throw new JuRuntimeException(String.format("No dynamic implementation found for %s and tagName=%s or defaultTagName=%s",
				clazz.getName(), tagName, defaultTagName));
		}
	}
	
	private <T> void addType(Class<?> clazz, Map<String, TypeCreator<T>> implementations, DynamicCdiTag tag, TypeCreator<T> typeCreator) {
		String tagValue = tag != null ? tag.value() : "-";
		
		logger.debug("Found implementation: {} (tag={})", typeCreator.toString(), tagValue);
		
		if (implementations.containsKey(tagValue)) {
			throw new JuRuntimeException(String.format(
					"Found two Simulatable implementations for %s and tag=%s: %s and %s (more might exist)",
					clazz.getName(), tagValue, implementations.get(tagValue).toString(), typeCreator.toString()));
		} else {
			implementations.put(tagValue, typeCreator);
		}
	}
	
	/**
	 * Helper interface to postpone object creation until we actually need one.
	 * @author martin.meyer@inftec.ch
	 *
	 * @param <T>
	 */
	private interface TypeCreator<T> {
		T createType();
	}
}
