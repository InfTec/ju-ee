package ch.inftec.ju.ee.test;

import ch.inftec.ju.util.JuUtils;
import ch.inftec.ju.util.PropertyChain;

/**
 * Utility class containing web test helper methods.
 * @author Martin
 *
 */
public class UrlTestUtils {
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
}
