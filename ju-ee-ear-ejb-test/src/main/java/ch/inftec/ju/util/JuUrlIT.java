package ch.inftec.ju.util;

import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.JuTestEnv;

/**
 * Test for VFS protocol substitution in JuUrl.
 * @author martin.meyer@inftec.ch
 *
 */
public class JuUrlIT extends ContainerTest {
	private static Logger logger = LoggerFactory.getLogger(JuUrlIT.class);

	private URL getResourceUrl() {
		return JuUrl.resourceRelativeTo("JuUrlIT_resource.txt", this.getClass());
	}
	
	/**
	 * Note that VFS resource lookup is enabled by default, i.e. no URL conversion will be attempted.
	 */
	@Test
	public void getsVfsResource_byDefault() {
		URL url = this.getResourceUrl();
		Assert.assertTrue(url.toExternalForm().startsWith("vfs:"));

		String txt = new IOUtil().loadTextFromUrl(url);
		Assert.assertEquals("Hello VFS", txt);
	}

	/**
	 * Test case to test activated VFS resource lookup.
	 * <p>
	 * We'll need to set the static fields of JuUrl accordingly as this value is cached...
	 */
	@Test
	@JuTestEnv(systemProperties = { "ju.ee.url.disableVfsForResourceLookup=true" })
	public void getsJarResource_ifFlagIsSet() {
		try {
			JuUrl.ResourceUrlBuilder.cacheVrfConversionFlag = null;
			JuUrl.ResourceUrlBuilder.disableVfsForResourceLookup = null;
			
			URL url = this.getResourceUrl();
			
			// When deployed on a server as a single EAR file, JBoss will have a VFS path that we cannot directly map
			// to a file. It will contain a 'content'. In order to avoid errors on remote builds, we'll accept this as well.
			// This feature is used mainly for local development anyway
			if (url.toExternalForm().contains("content")) {
				logger.info("URL contains 'content'. Not checking for conversion. " + url);
				return;
			} else {
				Assert.assertTrue(url.toExternalForm().startsWith("jar:file:"));

				String txt = new IOUtil().loadTextFromUrl(url);
				Assert.assertEquals("Hello VFS", txt);
			}
		} finally {
			JuUrl.ResourceUrlBuilder.cacheVrfConversionFlag = null;
			JuUrl.ResourceUrlBuilder.disableVfsForResourceLookup = null;
		}
	}

	/**
	 * Test for getResources (i.e. all, not just one)
	 */
	@Test
	public void getsVfsResources_byDefault() {
		List<URL> urls = JuUrl.resource().relativeTo(this.getClass()).getAll("JuUrlIT_resource.txt");
		
		Assert.assertEquals(1, urls.size());
		
		Assert.assertTrue(urls.get(0).toExternalForm().startsWith("vfs:"));
			
		String txt = new IOUtil().loadTextFromUrl(urls.get(0));
		Assert.assertEquals("Hello VFS", txt);
	}
}
