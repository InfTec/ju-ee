package ch.inftec.ju.ee.webtest;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.testing.db.DbTestAnnotationHandler;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.testing.db.SeleniumDriverPolicy;
import ch.inftec.ju.testing.db.SeleniumDriverPolicy.SeleniumDriver;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.JuStringUtils;
import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.PropertyChain;
import ch.inftec.ju.util.ReflectUtils;
import ch.inftec.ju.util.ReflectUtils.AnnotationInfo;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import ch.inftec.ju.util.TestUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * JUnit rule to run web tests with (multiple) selenium drivers.
 * @author Martin
 *
 */
public class DriverRule implements TestRule {
	private static final String CHROME_DRIVER_EXE_PROPERTY = "ju-testing-ee.selenium.chrome.chromeDriverExe";
	private static final String PHANTOM_JS_DRIVER_EXE_PROPERTY = "ju-testing-ee.selenium.phantomjs.phantomJsDriverExe";
	private static final String PROP_DRIVER = "ju-testing-ee.selenium.driver";
	
	private static final Logger logger = LoggerFactory.getLogger(DriverRule.class);
	
	private final WebTest testClass;
	
	
	// JUnit will create a new Rule instance for every test method run. Therefore, we'll use static references
	// to handle our drivers...
	private static String specifiedDrivers = null;
	private static List<DriverHandler> driverHandlers = new ArrayList<>();

	DriverRule(WebTest testClass) {
		this.testClass = testClass;
	}
	
	@Override
	public Statement apply(final Statement base, Description description) {
		// Handle JuTestEnv annotations
		Method method = TestUtils.getTestMethod(description);
		List<AnnotationInfo<JuTestEnv>> testEnvAnnos = ReflectUtils.getAnnotationsWithInfo(method, JuTestEnv.class, false, true, true);
		
		// Create a list of allowed selenium drivers
		List<AnnotationInfo<SeleniumDriverPolicy>> selPolicy =
				ReflectUtils.getAnnotationsWithInfo(method, SeleniumDriverPolicy.class, false, true, true);
		List<String> seleniumDriverPolicyStringList = Lists.newArrayList();
		if (selPolicy != null && !selPolicy.isEmpty()) {
			// The first entry is the most specific and overrides all others => just use this one
			SeleniumDriver[] seleniumPolicy = selPolicy.get(0).getAnnotation().value();
			logger.debug("SeleniumDriverPolicy found: [" + Joiner.on(",").join(seleniumPolicy) + "]");
			for (SeleniumDriver seleniumDriver : seleniumPolicy) {
				seleniumDriverPolicyStringList.add(seleniumDriver.name());
			}
		} else {
			logger.trace("No SeleniumDriverPolicy to apply");
		}

		Collections.reverse(testEnvAnnos);
		final SystemPropertyTempSetter tempSetter = DbTestAnnotationHandler.setTestEnvProperties(testEnvAnnos);
		
		try {
			PropertyChain pc = JuUtils.getJuPropertyChain();
			
			// We'll also check if currently specified drivers are still the same as the drivers we processed before.
			// This may change if we set test specified environment variables
			String currentSpecifiedDriversBeforePolicy = pc.get(PROP_DRIVER, true);
			String currentSpecifiedDrivers = null;

			// Check if the currently set driver is part of the driver policy list
			if (!seleniumDriverPolicyStringList.isEmpty()) {
				String drivers[] = JuStringUtils.split(currentSpecifiedDriversBeforePolicy, ",", true);
				List<String> cleanedDriverList = Lists.newArrayList();
				for (String driver : drivers) {
					if (seleniumDriverPolicyStringList.contains(driver)) {
						cleanedDriverList.add(driver);
					} else {
						logger.debug("Removed the driver '" + driver
								+ "' from the driver list, as it not part of the set SeleniumDriverPolicy");
					}
				}
				// Set default if necessary
				if (cleanedDriverList.isEmpty()) {
					cleanedDriverList.add(seleniumDriverPolicyStringList.get(0));
				}
				currentSpecifiedDrivers = Joiner.on(",").join(cleanedDriverList);
			} else {
				currentSpecifiedDrivers = currentSpecifiedDriversBeforePolicy;
			}

			if (DriverRule.driverHandlers.isEmpty() || !StringUtils.equals(DriverRule.specifiedDrivers, currentSpecifiedDrivers)) {
				DriverRule.specifiedDrivers = currentSpecifiedDrivers;
				closeAll();
				
				// Get from the properties which drivers we should use to run the tests 
				String drivers[] = JuStringUtils.split(DriverRule.specifiedDrivers, ",", true);
				Assert.assertTrue(
						String.format("No drivers specified, please check '%s' and the SeleniumDriverPolicy for the JUnit test",
								DriverRule.PROP_DRIVER), drivers.length > 0);
				
				logger.debug("Initialize WebDrivers: " + Arrays.toString(drivers));
				for (String driverType : drivers) {
					logger.debug("Creating driver: " + driverType);
					
					if (SeleniumDriverPolicy.SeleniumDriver.HtmlUnit.name().equals(driverType)) {
						DriverRule.driverHandlers.add(new HtmlUnitDriverHandler());
					} else if (SeleniumDriverPolicy.SeleniumDriver.Chrome.name().equals(driverType)) {
						DriverRule.driverHandlers.add(new ChromeDriverHandler());
					} else if (SeleniumDriverPolicy.SeleniumDriver.PhantomJS.name().equals(driverType)) {
						DriverRule.driverHandlers.add(new PhantomJSDriverHandler());
					} else {
						throw new JuRuntimeException(String.format("Unsupported selenium driver type: %s. Check value of property %s"
								, driverType
								, PROP_DRIVER));
					}
				}
			}
			
			return new Statement() {
				public void evaluate() throws Throwable {
					try {
						// Run test case for with all drivers.
						// We cannot use a for-iterator here as the closeAll method gets called when the for
						// method tries to loop again, resulting in a ConcurrentModificationException.
						for (int i = 0; i < driverHandlers.size(); i++) {
							DriverHandler driverHandler = driverHandlers.get(i);
							try (DriverHandler.DriverHandlerCreator driverCreator = driverHandler.newDriverHandlerCreator()) {
								logger.info("Running test with WebDriver " + driverCreator);
								testClass.driver = driverCreator.getDriver();

								// If the evaluation fails, it will break our loop, i.e. if we want to run drivers
								// d1 and d2 and in d1, we have an exception, d2 won't be executed at all...
								base.evaluate();
							}
						}
					} finally {
						tempSetter.close();
					}
				}
			};
		} catch (Exception ex) {
			tempSetter.close();
			throw ex;
		}
	}
	
	/**
	 * Disposes all open resources a DriverRule may hold.
	 * <p>
	 * This should be called after the test class has been executed.
	 */
	public static void closeAll() {
		// Clear DriverHandlers. This way, we may specify drivers to use in a system property.
		driverHandlers.clear();
	}

	private static abstract class DriverHandler {
		protected final PropertyChain pc = JuUtils.getJuPropertyChain();

		public DriverHandlerCreator newDriverHandlerCreator() {
			return new DriverHandlerCreator();
		}

		/**
		 * Gets the proxy server to be used.
		 * 
		 * @return Proxy server or null if no proxy should be used
		 */
		protected final String getProxyServer() {
			String proxyServer = pc.get("ju-testing-ee.internet.proxy");
			return StringUtils.isEmpty(proxyServer) ? null : proxyServer;
		}

		private class DriverHandlerCreator implements AutoCloseable {
			private final WebDriver driver;

			public DriverHandlerCreator() {
				this.driver = createWebDriver();
			}

			public WebDriver getDriver() {
				return driver;
			}

			@Override
			public void close() throws Exception {
				logger.debug("Closing WebDriver " + this.toString());
				this.driver.quit();
			}

			@Override
			public String toString() {
				return String.format("%s [WebDriver: %s]", DriverHandler.this.getClass().getSimpleName(), this.driver);
			}
		}

		protected abstract WebDriver createWebDriver();
	}

	public static class HtmlUnitDriverHandler extends DriverHandler {
		@Override
		protected WebDriver createWebDriver() {
			DesiredCapabilities cap = DesiredCapabilities.htmlUnit();
			
			// Configure proxy server
			String proxyServer = getProxyServer();
			if (proxyServer != null) {
				Proxy proxy = new Proxy();
				proxy.setHttpProxy(proxyServer);

				cap.setCapability(CapabilityType.PROXY, proxy);
			}

			// Configure JavaScript
			boolean enableJavaScript = pc.get("ju-testing-ee.selenium.htmlUnit.enableJavascript", Boolean.class);
			cap.setJavascriptEnabled(enableJavaScript);

			return new HtmlUnitDriver(cap);
		}
	}

	public static class ChromeDriverHandler extends DriverHandler {
		@Override
		protected WebDriver createWebDriver() {
			System.setProperty("webdriver.chrome.driver",
					DriverRule.getDriverExePath(DriverRule.CHROME_DRIVER_EXE_PROPERTY).toAbsolutePath().toString());

			DesiredCapabilities cap = DesiredCapabilities.chrome();
			
			// Configure Proxy
			String proxyServer = getProxyServer();
			if (proxyServer != null) {
				// ChromeDriver accepts both, proxy:8080 and http://proxy:8080
				cap.setCapability("chrome.switches", Arrays.asList(String.format("--proxy-server=%s", proxyServer)));
			}

			return new ChromeDriver(cap);
		}
	}
	
	public static class PhantomJSDriverHandler extends DriverHandler {
		@Override
		protected WebDriver createWebDriver() {
			DesiredCapabilities cap = DesiredCapabilities.phantomjs();
			
			// Configure Proxy
			String proxyServer = getProxyServer();
			if (proxyServer != null) {
				// ChromeDriver accepts both, proxy:8080 and http://proxy:8080
				ArrayList<String> cliArgsCap = new ArrayList<String>();
				cliArgsCap.add("--proxy=" + proxyServer + ":8080");
				cliArgsCap.add("--proxy-type=http");
				
				cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
			}
			
			// PhantomJS exe path
			String phantomJSExePath = DriverRule.getDriverExePath(DriverRule.PHANTOM_JS_DRIVER_EXE_PROPERTY).toAbsolutePath().toString();
			
			cap.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSExePath);
			
			return new PhantomJSDriver(cap);
		}
	}
	
	private static Path getDriverExePath(String propertyName) {
		// Check if the chromedriver.exe path was set in the properties
		String propertiesDriverPathString = JuUtils.getJuPropertyChain().get(propertyName);
		if (!StringUtils.isEmpty(propertiesDriverPathString)) {
			Path juDriverExePath = Paths.get(propertiesDriverPathString);
			logger.debug("Using the path '" + juDriverExePath + "' from JU settings for: " + propertyName);
			
			return juDriverExePath;
		} else {
			throw new JuRuntimeException("The property '" + propertyName + "' (path to the executable') is not set");
		}
	}
}
