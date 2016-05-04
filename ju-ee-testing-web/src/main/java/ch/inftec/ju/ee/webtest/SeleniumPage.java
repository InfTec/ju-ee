package ch.inftec.ju.ee.webtest;

import org.openqa.selenium.WebDriver;

/**
 * Base class for Selenium Page classes.
 * @author Martin
 *
 */
public abstract class SeleniumPage {
	/**
	 * WebDriver object used by this page.
	 */
	protected final WebDriver driver;
	
	protected SeleniumPage(WebDriver driver) {
		this.driver = driver;
	}
	
	/**
	 * Gets the page specified with the pageUrl, e.g. web-app/page.jsf.
	 * @param pageUrl Page URL without protocol, hostname and port
	 */
	protected final void getPage(String pageUrl) {
		WebTestUtils.getPage(this.driver, pageUrl);
	}
}
