package ch.inftec.ju.ee.client;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.PropertyChain;

/**
 * Builder to create ServiceLocator instances.
 * @author Martin
 *
 */
public final class ServiceLocatorBuilder {
	/**
	 * Gets a builder to create a new Remote ServiceLocator, i.e. a ServiceLocator
	 * that will perform remote JBoss lookups.
	 * <p>
	 * Uses remote-naming for the lookup.
	 * @return Builder instance
	 */
	public static RemoteServiceLocatorBuilder buildRemote() {
		return new RemoteServiceLocatorBuilder();
	}
	
	/**
	 * Builds a remote service locator based on the values of configuration properties
	 * using the default JU Property chain.
	 * 
	 * @return Remote service locator
	 */
	public static JndiServiceLocator createRemoteByConfigurationFiles() {
		PropertyChain pc = JuUtils.getJuPropertyChain();
		
		Integer port = pc.get("ju-util-ee.remote.port", Integer.class, true);
		Integer portOffset = pc.get("ju-util-ee.portOffset", Integer.class, true);
		
		return buildRemote()
			.remoteServer(pc.get("ju-util-ee.remote.host", true), port + portOffset)
			.appName(pc.get("ju-util-ee.remote.appName", true))
			.moduleName(pc.get("ju-util-ee.remote.moduleName", true))
			.createServiceLocator();
	}
	
	/**
	 * Gets a builder to create a new Local ServiceLocator, i.e. a ServiceLocator
	 * that will perform local JNDI and CDI lookups.
	 * @return Builder instance
	 */
	public static LocalServiceLocatorBuilder buildLocal() {
		return new LocalServiceLocatorBuilder();
	}
	
	/**
	 * Creates a local (CDI) ServiceLocator using the specified BeanManager implementation.
	 * <p>
	 * The BeanManager should be provided by the CDI container implementation we're using (e.g. Weld) 
	 * @param bm BeanManager used to perform the CDI lookups
	 * @return ServiceLocator to perform local CDI lookups
	 */
	public static ServiceLocator createLocalByBeanManager(BeanManager bm) {
		return new LocalServiceLocatorImpl(bm);
	}
	
	/**
	 * Helper class to build remote ServiceLocator instances
	 * @author Martin
	 *
	 */
	public static class RemoteServiceLocatorBuilder {
		private Logger logger = LoggerFactory.getLogger(RemoteServiceLocatorBuilder.class);
		
		private String host = "localhost";
		private int port = 8080;
		private String appName;
		private String moduleName;

		/**
		 * Initializes the remote service locator based on the values of configuration properties
		 * using the default JU Property chain.
		 * 
		 * @return Builder to adapt configuration
		 */
		public RemoteServiceLocatorBuilder initByConfigurationFiles() {
			PropertyChain pc = JuUtils.getJuPropertyChain();

			Integer port = pc.get("ju-util-ee.remote.port", Integer.class, true);
			Integer portOffset = pc.get("ju-util-ee.portOffset", Integer.class, true);

			this.remoteServer(pc.get("ju-util-ee.remote.host", true), port + portOffset);
			this.appName(pc.get("ju-util-ee.remote.appName", true));
			this.moduleName(pc.get("ju-util-ee.remote.moduleName", true));

			return this;
		}

		/**
		 * Sets the remote host and port for the lookup.
		 * @param host Remote host. Default is localhost
		 * @param port Remote port. Default is 4447
		 * @return This builder to allow for chaining
		 */
		public RemoteServiceLocatorBuilder remoteServer(String host, int port) {
			this.host = host;
			this.port = port;
			return this;
		}
		
		/**
		 * Sets the application name. This is usually the name of the EAR without
		 * the .ear suffix, e.g. 'test' for test.ear
		 * @param appName Application name
		 * @return This builder to allow for chaining
		 */
		public RemoteServiceLocatorBuilder appName(String appName) {
			this.appName = appName;
			return this;
		}
		
		/**
		 * Sets the module name. This is usually the name of the EJB jar that contains
		 * the EJB bean without the .jar suffix, e.g. 'test' for test.jar
		 * @param moduleName Module name
		 * @return This builder to allow for chaining
		 */
		public RemoteServiceLocatorBuilder moduleName(String moduleName) {
			this.moduleName = moduleName;
			return this;
		}
		
		/**
		 * Creates a new RemoteServiceLocator instance with the attributes specified
		 * to the builder.
		 * @return JndiServiceLocator instance
		 */
		public JndiServiceLocator createServiceLocator() {
			try {
				// Set EJB Client API properties programmatically instead of using
				// jboss-ejb-client.properties file
				Properties clientProp = new Properties();
				clientProp.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
				clientProp.put("remote.connections", "default");
				clientProp.put("remote.connection.default.port", Integer.toString(this.port)); // Not working if not a String...
				clientProp.put("remote.connection.default.host", this.host);
//				clientProp.put("remote.connection.default.username", "ejbUser");
//				clientProp.put("remote.connection.default.password", "ejbPassword");
				clientProp.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
				
				// PR-284 Don't seem to need this anymore...
				EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(clientProp);
				ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
				EJBClientContext.setSelector(selector);
				
				logger.debug("JBoss EJB Client Properties (used for remote lookup)" + clientProp);
				
				Properties props = new Properties();
				props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
				Context ctx = new InitialContext(props);
				
//				Properties jndiProps = new Properties();
//				jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//				jndiProps.put(Context.PROVIDER_URL, String.format("remote://%s:%d", this.host, this.port));
//				// create a context passing these properties
//				Context ctx = new InitialContext(jndiProps);
				
				logger.debug("Creating RemoteServiceLocator [host={}:{}, app={} / module={}"
						, new Object[] {this.host, this.port, this.appName, this.moduleName});
				
				return new RemoteServiceLocatorImpl(ctx, this.appName, this.moduleName);
			} catch (Exception ex) {
				throw new JuRuntimeException("Couldn't create ServiceLocator", ex);
			}
		}
	}
	
	/**
	 * Helper class to build local ServiceLocator instances
	 * @author Martin
	 *
	 */
	public static class LocalServiceLocatorBuilder {
		private String moduleName;
		
		/**
		 * Sets the module name. This is usually the name of the EJB jar that contains
		 * the EJB bean without the .jar suffix, e.g. 'test' for test.jar
		 * <p>
		 * Is used to look up remote interfaces in the same (local) app.
		 * @param moduleName Module name
		 * @return This builder to allow for chaining
		 */
		public LocalServiceLocatorBuilder moduleName(String moduleName) {
			this.moduleName = moduleName;
			return this;
		}
		
		/**
		 * Creates a new LocalServiceLocatorBuilder instance with the attributes specified
		 * to the builder.
		 * @return ServiceLocator instance
		 */
		public ServiceLocator createServiceLocator() {
			return new LocalServiceLocatorImpl(this.moduleName);
		}
	}
		
	
	private static abstract class AbstractJndiServiceLocator implements JndiServiceLocator {
		protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
		private final Context ctx;
		
		protected AbstractJndiServiceLocator(Context ctx) {
			this.ctx = ctx;
		}
		
		/**
		 * Gets the absolute JNDI name (i.e. the JNDI name that will actually be used
		 * for the lookup) based on the submitted relative JNDI name.
		 * <p>
		 * The default implementation just returns the same JNDI
		 * @param jndiName (Relative) JNDI name
		 * @return Absolute JNDI name used to perform the lookup
		 */
		protected String getAbsoluteJndiName(String jndiName) {
			return jndiName;
		}
		
		@Override
		public <T> T lookup(String jndiName) {
			String absoluteJndiName = this.getAbsoluteJndiName(jndiName);
			logger.debug(String.format("JNDI lookup (relative: %s, absolute: %s)", jndiName, absoluteJndiName));
			
			try {
				@SuppressWarnings("unchecked")
				T obj = (T) this.ctx.lookup(absoluteJndiName);
				
				return obj;
			} catch (Exception ex) {
				throw new JuRuntimeException(ex);
			}
		}
	}
	
	private static class RemoteServiceLocatorImpl extends AbstractJndiServiceLocator {
		private final String appName;
		private final String moduleName;
		
		public RemoteServiceLocatorImpl(Context ctx, String appName, String moduleName) {
			super(ctx);

			this.appName = appName;
			this.moduleName = moduleName;
		}

		@Override
		protected String getAbsoluteJndiName(String jndiName) {
			// See https://github.com/wildfly/quickstart/blob/10.x/ejb-remote/client/src/main/java/org/jboss/as/quickstarts/ejb/remote/client/RemoteEJBClient.java
			
			// The app name is the application name of the deployed EJBs. This is typically the ear name
			// without the .ear suffix. However, the application name could be overridden in the application.xml of the
			// EJB deployment on the server.
			// Since we haven't deployed the application as a .ear, the app name for us will be an empty string
			final String appName = this.appName;
			// This is the module name of the deployed EJBs on the server. This is typically the jar name of the
			// EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
			// In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
			// jboss-as-ejb-remote-app
			final String moduleName = this.moduleName;
			// AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
			// our EJB deployment, so this is an empty string
			final String distinctName = ""; // We don't have this
			
			// jndiName must contain the EJB Name and the viewClassName, e.g. GreeterBean!com.acme.Greeter
			
			String absoluteJndiName = String.format("ejb:%s/%s/%s/%s"
					, appName
					, moduleName
					, distinctName
					, jndiName);
			
			return absoluteJndiName;
		}
		
		@Override
		public <T> T lookup(Class<T> clazz) {
			String lookupString = String.format("%sBean!%s"
					, clazz.getSimpleName()
					, clazz.getName());
			
			return this.lookup(lookupString);
		}
	}
	
	private static class LocalServiceLocatorImpl extends AbstractJndiServiceLocator implements ServiceLocator {
		private static final String JNDI_NAME_BEAN_MANAGER = "java:comp/BeanManager";
		
		private final String moduleName;
		private final BeanManager bm;
		
		private LocalServiceLocatorImpl(BeanManager bm) {
			super(createInitialContext());
			
			this.moduleName = "";
			this.bm = bm;
		}
		
		private LocalServiceLocatorImpl(String moduleName) {
			super(createInitialContext());

			this.moduleName = moduleName == null ? "" : moduleName;
			this.bm = this.lookup(JNDI_NAME_BEAN_MANAGER);
		}
		
		private static Context createInitialContext() {
			try {
				return new InitialContext();
			} catch (Exception ex) {
				throw new JuRuntimeException("Couldn't create InitialContext", ex);
			}
		}

		@Override
		public <T> T lookup(Class<T> clazz) {
//			java:global/ee-ear-ear/ee-ear-ejb/TestLocalBean!ch.inftec.ju.ee.test.TestLocal
//			java:app/ee-ear-ejb/TestLocalBean!ch.inftec.ju.ee.test.TestLocal
//			java:module/TestLocalBean!ch.inftec.ju.ee.test.TestLocal
//			java:global/ee-ear-ear/ee-ear-ejb/TestLocalBean
//			java:app/ee-ear-ejb/TestLocalBean
//			java:module/TestLocalBean
			
			String jndiName = String.format("java:app/%s/%s!%s"
					, this.moduleName
					, clazz.getSimpleName() + "Bean"
					, clazz.getName());
			
			return this.lookup(jndiName);
		}

		@Override
		public <T> T cdi(Class<T> clazz) {
			return cdiAnno(clazz);
		}

		@Override
		public <T> T cdiAnno(Class<T> clazz, Annotation... annotations) {
			Set<Bean<?>> beans = this.bm.getBeans(clazz, annotations);

			// We'll use resolve to get a single instance (e.g. we might have @Alternative beans and getBeans will include them
			Bean<?> bean = this.bm.resolve(beans);
			return ServiceLocatorUtils.toInstance(this.bm, bean, clazz);
		}
		
		@Override
		public <T> List<T> cdiAll(Class<T> clazz) {
			return cdiAllAnno(clazz);
		}

		@Override
		public <T> List<T> cdiAllAnno(Class<T> clazz, Annotation... annotations) {
			Set<Bean<?>> beans = this.bm.getBeans(clazz, annotations);
			return ServiceLocatorUtils.toInstances(this.bm, beans, clazz, null);
		}

		@Override
		public <T> T cdiNamed(Class<T> clazz, final String name) {
			Set<Bean<?>> beans = this.bm.getBeans(clazz, ServiceLocatorUtils.createNamedAnnotation(name));
			return ServiceLocatorUtils.toInstances(this.bm, beans, clazz, 1).get(0);
		}

		@Override
		public <T> CdiComplexLookupBuilder<T> cdiComplex(Class<T> clazz) {
			return new CdiComplexLookupBuilder<T>(clazz, this.bm);
		}
	}
}
