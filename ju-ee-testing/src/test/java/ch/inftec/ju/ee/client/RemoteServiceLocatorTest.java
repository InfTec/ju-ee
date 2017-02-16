package ch.inftec.ju.ee.client;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
//import org.jboss.ejb.client.ContextSelector;
//import org.jboss.ejb.client.EJBClientConfiguration;
//import org.jboss.ejb.client.EJBClientContext;
//import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
//import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.test.AbstractTestBean;
import ch.inftec.ju.ee.test.TestRemote;
import ch.inftec.ju.ee.test.TestRemoteBean;
import ch.inftec.ju.util.JuUrl;

/**
 * Tests EJB remote invocation with Wildfly 10.
 * See https://github.com/wildfly/quickstart/tree/10.x/ejb-remote for an example.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
@RunWith(Arquillian.class)
public class RemoteServiceLocatorTest {
	/**
	 * Set testable to false to avoid having test run in server JVM. Instead, it will run in the client JVM.
	 */
	@Deployment(testable = false)
	public static JavaArchive createDeployment() {
		JavaArchive war = ShrinkWrap.create(JavaArchive.class, "test.jar");

		war.addClass(AbstractTestBean.class);
		war.addClass(TestRemote.class);
		war.addClass(TestRemoteBean.class);

		war.addClass(RemoteCalculator.class);
		war.addClass(CalculatorBean.class);

		// Define stateless remote bean in ejb-jar.xml
		war.addAsManifestResource(
				JuUrl.existingResourceRelativeTo("RemoteServiceLocatorTest_ejb-jar.xml", RemoteServiceLocatorTest.class),
				"ejb-jar.xml");

		return war;
	}

	@Test
	public void canLookup_testFacadeBean_usingEjbClientApi() throws Exception {
		// https://docs.jboss.org/author/display/AS72/Remote+EJB+invocations+via+JNDI+-+EJB+client+API+or+remote-naming+project
		
		// WAR
		/*
		 * java:global/test/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
		 * java:app/test/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
		 * java:module/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
		 * java:jboss/exported/test/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
		 * java:global/test/TestRemoteInterfaceBean
		 * java:app/test/TestRemoteInterfaceBean
		 * java:module/TestRemoteInterfaceBean
		 */

		// EAR
//		14:57:28,691 INFO  [org.jboss.as.ejb3.deployment.processors.EjbJndiBindingsDeploymentUnitProcessor] (MSC service thread 1-13) JNDI bindings for session bean named TestRemoteInterfaceBean in deployment unit subdeployment "ee-ear-ejb.jar" of deployment "ee-ear-ear.ear" are as follows:
//
//			java:global/ee-ear-ear/ee-ear-ejb/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
//			java:app/ee-ear-ejb/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
//			java:module/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
//			java:jboss/exported/ee-ear-ear/ee-ear-ejb/TestRemoteInterfaceBean!ch.inftec.ju.ee.test.TestRemote
//			java:global/ee-ear-ear/ee-ear-ejb/TestRemoteInterfaceBean
//			java:app/ee-ear-ejb/TestRemoteInterfaceBean
//			java:module/TestRemoteInterfaceBean
		
		// AppName     : ee-ear
		// ModuleName  : ee-ear-ejb
		// DistinctName: TestRemoteInterfaceBean
		
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
//		jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:14447");
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
		
        // Important: We need to use the 'ejb:' prefix...
		TestRemote testRemote = (TestRemote) context.lookup("ejb:/test//TestRemoteInterfaceBean!" + TestRemote.class.getName());
		Assert.assertEquals("TestRemoteBean says hello to EjbClientApi", testRemote.getGreeting("EjbClientApi"));
	}
	
	@Test
	public void canLookup_remoteCalculator_usingEjbClientApi() throws Exception {
		RemoteCalculator calculator = lookupRemoteStatelessCalculator();

		assertEquals(3, calculator.add(1, 2));
	}

	/**
	 * Looks up and returns the proxy to remote stateless calculator bean
	 *
	 * @return
	 * @throws NamingException
	 */
	private static RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		// jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:44473");
		final Context context = new InitialContext(jndiProperties);
		// The app name is the application name of the deployed EJBs. This is typically the ear name
		// without the .ear suffix. However, the application name could be overridden in the application.xml of the
		// EJB deployment on the server.
		// Since we haven't deployed the application as a .ear, the app name for us will be an empty string
		final String appName = "";
		// This is the module name of the deployed EJBs on the server. This is typically the jar name of the
		// EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
		// In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
		// jboss-as-ejb-remote-app
		final String moduleName = "test";
		// AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
		// our EJB deployment, so this is an empty string
		final String distinctName = "";
		// The EJB name which by default is the simple class name of the bean implementation class
		final String beanName = CalculatorBean.class.getSimpleName();
		// the remote view fully qualified class name
		final String viewClassName = RemoteCalculator.class.getName();
		// let's do the lookup
		return (RemoteCalculator) context
				.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
	}

	@Test
	public void canLookup_testFacadeBean_usingEjbClientApi_withoutFile() throws Exception {
		// Set EJB Client API properties programmatically instead of using
		// jboss-ejb-client.properties file
		Properties clientProp = new Properties();
		clientProp.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		clientProp.put("remote.connections", "default");
		clientProp.put("remote.connection.default.port", "18080");
		clientProp.put("remote.connection.default.host", "localhost");
//		clientProp.put("remote.connection.default.username", "ejbUser");
//		clientProp.put("remote.connection.default.password", "ejbPassword");
		clientProp.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

		// Set selector (avoiding usage of jboss-ejb-client.properties file)
		EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(clientProp);
		ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
		EJBClientContext.setSelector(selector);
		 
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
//		jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:14447");
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
		
        // Important: We need to use the 'ejb:' prefix...
		TestRemote testRemote = (TestRemote) context.lookup("ejb:/test//TestRemoteInterfaceBean!" + TestRemote.class.getName());
		Assert.assertEquals("TestRemoteBean says hello to EjbClientApi", testRemote.getGreeting("EjbClientApi"));
	}
	
	/**
	 * Note that the remote naming tests were not really using the properties submitted to them
	 * by the Properties instance. Instead, the file jboss-ejb-client.properties was read, same as
	 * with the EJB Client API...
	 * @throws Exception
	 */
	@Ignore("PR-285 Not working with WildFly 10, didn't investigate further. We probably don't need this...")
	@Test
	public void canLookup_testFacadeBean_usingRemoteNaming() throws Exception {
		// https://docs.jboss.org/author/display/AS72/Remote+EJB+invocations+via+JNDI+-+EJB+client+API+or+remote-naming+project
		
		Properties jndiProps = new Properties();
		jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		jndiProps.put(Context.PROVIDER_URL, "remote://localhost:14447");
		// jndiProps.put("remote.connection.default.port", "8080");
		// create a context passing these properties
		Context ctx = new InitialContext(jndiProps);

		// Important: We can either use 'java:' as prefix or none
		TestRemote testRemote = (TestRemote) ctx.lookup("test/TestRemoteInterfaceBean!" + TestRemote.class.getName());
		Assert.assertEquals("TestRemoteBean says hello to RemoteNamingWithoutPrefix", testRemote.getGreeting("RemoteNamingWithoutPrefix"));
		
		testRemote = (TestRemote) ctx.lookup("java:ee-ear-ear/ee-ear-ejb/TestRemoteBean!" + TestRemote.class.getName());
		Assert.assertEquals("TestRemoteBean says hello to RemoteNaming", testRemote.getGreeting("RemoteNaming"));
	}
	
	@Test
	public void canLookup_testFacadeBean_usingServiceLocatorBuilder() throws Exception {
		JndiServiceLocator loc = ServiceLocatorBuilder.buildRemote()
				.remoteServer("localhost", 18080)
				.appName("")
				.moduleName("test")
			.createServiceLocator();
		
		// TestRemote testRemote = loc.lookup(TestRemote.class);
		TestRemote testRemote = loc.lookup("TestRemoteInterfaceBean!" + TestRemote.class.getName());
		Assert.assertEquals("TestRemoteBean says hello to ServiceLocatorBuilder", testRemote.getGreeting("ServiceLocatorBuilder"));
	}
}
