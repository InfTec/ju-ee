package ch.inftec.ju.ee.webtest;

import org.junit.AfterClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for test cases that run selenium web tests.
 * <p>
 * See ju-testing-ee_default.properties on how to configure tests.
 * @author Martin
 *
 */
public abstract class WebTest {
	protected WebDriver driver;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Rule
	public DriverRule driverRule = new DriverRule(this);
	
	@AfterClass
	public static void closeDriver() {
		DriverRule.closeAll();
	}

	/**
	 * Gets the page specified with the pageUrl, e.g. web-app/page.jsf.
	 * 
	 * @param pageUrl
	 *            Page URL without protocol, hostname and port
	 */
	protected final void getPage(String pageUrl) {
		WebTestUtils.getPage(this.driver, pageUrl);
	}
}
