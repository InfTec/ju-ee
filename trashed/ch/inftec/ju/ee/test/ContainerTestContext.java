package ch.inftec.ju.ee.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.util.JuRuntimeException;

/**
 * Context that covers the lifetime of a ContainerTest test method.
 * <p>
 * If not running in a ContainerTest scope, the context will
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
public class ContainerTestContext implements Context {
	private static Logger logger = LoggerFactory.getLogger(ContainerTestContext.class);

	/**
	 * Context for the current Thread.
	 */
	private static ThreadLocal<UUID> contextUuid = new ThreadLocal<>();

	/**
	 * Set of active contexts. We need this in order to reliably know whether the context assigned to a Thread is
	 * actually still active.
	 */
	private static Set<UUID> activeContexts = new HashSet<>();

	/**
	 * Map that maps contexts to maps of (class <- 1-* -> list of customScopeInstance) objects.
	 * <p>
	 * We need to take multiple CustomScopeInstances into consideration to be able to handle different contextuals (annotations).
	 */
	private static Map<UUID, Map<Class<?>, List<CustomScopeInstance<?>>>> allInstances = new HashMap<UUID, Map<Class<?>, List<CustomScopeInstance<?>>>>();

	private static synchronized Object getObject(Class<?> clazz, Contextual<?> contextual) {
		if (contextUuid.get() != null
				&& allInstances.containsKey(contextUuid.get())
				&& allInstances.get(contextUuid.get()).containsKey(clazz)) {

			List<CustomScopeInstance<?>> csis = allInstances.get(contextUuid.get()).get(clazz);

			// Look if we have a CustomScopeInstance that complies to the specified contextual
			for (CustomScopeInstance<?> csi : csis) {
				// TODO: Can we use reference comparison here???
				if (csi.bean == contextual) {
					return csi.instance;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	private static synchronized <T> T storeObject(Class<?> clazz, CustomScopeInstance<T> inst) {
		if (contextUuid.get() != null) {
			if (!allInstances.containsKey(contextUuid.get())) {
				allInstances.put(contextUuid.get(), new HashMap<Class<?>, List<CustomScopeInstance<?>>>());
			}

			// Check if we already have a list for the class
			if (!allInstances.get(contextUuid.get()).containsKey(clazz)) {
				allInstances.get(contextUuid.get()).put(clazz, new ArrayList<CustomScopeInstance<?>>());
			}
			allInstances.get(contextUuid.get()).get(clazz).add(inst);
			return inst.instance;
		} else {
			throw new JuRuntimeException("No active context");
		}
	}

	static synchronized void startContext(UUID uuid) {
		logger.debug("Starting context: " + uuid);
		contextUuid.set(uuid);
		activeContexts.add(uuid);
	}

	static synchronized <T> void endContext(boolean dispose) {
		UUID id = contextUuid.get();
		logger.debug("Ending context [dispose={}]: {}", dispose, id);
		contextUuid.set(null);
		activeContexts.remove(id);

		if (dispose) {
			if (id != null && allInstances.containsKey(id)) {
				for (List<CustomScopeInstance<?>> insts : allInstances.get(id).values()) {
					for (CustomScopeInstance<?> inst : insts) {
						inst.destroy();
					}
				}
				allInstances.remove(id);
			}
		}
	}

	public static class CustomScopeInstance<T> {
		final Bean<T> bean;
		final CreationalContext<T> ctx;
		final T instance;

		public CustomScopeInstance(Bean<T> bean, CreationalContext<T> ctx) {
			this.bean = bean;
			this.ctx = ctx;
			this.instance = bean.create(this.ctx);
		}

		public void destroy() {
			bean.destroy(instance, ctx);
		}

	}

	@Override
	public Class<? extends Annotation> getScope() {
		return ContainerTestScoped.class;
	}

	@Override
	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
		T t = this.get(contextual);
		if (t == null) {
			Bean<T> b = (Bean<T>) contextual;
			return storeObject(b.getBeanClass(), new CustomScopeInstance<T>(b, creationalContext));
		} else {
			return t;
		}
	}

	@Override
	public <T> T get(Contextual<T> contextual) {
		Bean<T> b = (Bean<T>) contextual;

		@SuppressWarnings("unchecked")
		T t = (T) getObject(b.getBeanClass(), contextual);
		return t;
	}

	@Override
	public boolean isActive() {
		return isContextActive();
	}

	/**
	 * Static method to evaluate if the context is active, i.e. if we're ina test method environment.
	 * <p>
	 * Note that CDI will create the proxy in any case, but if the scope is not active, accessing it will throw a ContextNotActiveException.
	 * 
	 * @return
	 */
	public static synchronized boolean isContextActive() {
		UUID id = contextUuid.get();
		return id != null && activeContexts.contains(id);
	}
}
