package ch.inftec.ju.ee.webtest;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.test.UrlTestUtils;

/**
 * Utility class containing web test helper methods.
 * @author Martin
 *
 */
public class WebTestUtils {
	private static Logger logger = LoggerFactory.getLogger(WebTestUtils.class);
	
	/**
	 * Gets the full page URL for an URL specified without host and port,
	 * e.g. <code>web-app/page.jsf</code>. Host name and port will be resolved from the
	 * JU properties, e.g. <code>http://localhost:8080/web-app/page.jsf</code>
	 * 
	 * @param subPageUrl
	 *            Page URL without host an port
	 * @deprecated Use UrlTestUtils.getPageUrl instead
	 */
	@Deprecated
	public static String getPageUrl(String subPageUrl) {
		return UrlTestUtils.getPageUrl(subPageUrl);
	}

	/**
	 * Opens the specified page. The page URL must be specified without host and port,
	 * e.g. <code>web-app/page.jsf</code>. Host name and port will be resolved from the
	 * JU properties, e.g. <code>http://localhost:8080/web-app/page.jsf</code>
	 * 
	 * @param driver
	 *            WebDriver to get page for
	 * @param subPageUrl
	 *            Page URL
	 */
	public static void getPage(WebDriver driver, String subPageUrl) {
		String url = UrlTestUtils.getPageUrl(subPageUrl);
		logger.debug("Getting " + url);
		
		driver.get(url);	
	}
}
