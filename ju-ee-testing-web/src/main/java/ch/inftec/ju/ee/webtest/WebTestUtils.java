package ch.inftec.ju.ee.webtest;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.PropertyChain;

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
	 */
	public static String getPageUrl(String subPageUrl) {
		PropertyChain pc = JuUtils.getJuPropertyChain();

		String host = pc.get("ju-testing-ee.web.host");
		Integer port = pc.get("ju-testing-ee.web.port", Integer.class) + pc.get("ju-util-ee.portOffset", Integer.class);

		String url = String.format("http://%s:%d/%s", host, port, subPageUrl);
		return url;
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
		String url = WebTestUtils.getPageUrl(subPageUrl);
		logger.debug("Getting " + url);
		
		driver.get(url);	
	}
}
