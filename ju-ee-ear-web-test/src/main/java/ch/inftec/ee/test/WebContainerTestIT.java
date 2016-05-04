package ch.inftec.ee.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import ch.inftec.ju.ee.test.WebContainerTest;
import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.JuAssumeUtils;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.util.SystemPropertyTempSetter;
import ch.inftec.ju.util.TestUtils;

public class WebContainerTestIT extends WebContainerTest {
	@Test
	public void canAccess_staticPage() throws Exception {
		this.getPage("ju-ee-ear-web/static.html");

		Assert.assertEquals("Static Title", driver.getTitle());
		Assert.assertEquals("Static Body", driver.findElement(By.id("text")).getText());
	}
	
	@Test
	@DataSet("WebContainerTestIT_onePlayer.xml")
	public void canLoadTestData_andAccessDynamicPage() {
		this.getPage("ju-ee-ear-web/simpleJsf.jsf");

		Assert.assertEquals("1", driver.findElement(By.id("playerCntEm")).getText());
	}
	
	@Test
	@DataSet(value="ju-testing/data/default-noData.xml"
			, inserts={"WebContainerTestIT_onePlayer.xml"})
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml")
	@DataVerify
	public void canLoadTestData_andVerify_byXml() {
		this.getPage("ju-ee-ear-web/simpleJsf.jsf");

		Assert.assertEquals("1", driver.findElement(By.id("playerCntEm")).getText());
	}
	public static class CanLoadTestData_andVerify_byXml extends DataVerifier {
		@Override
		public void verify() throws Exception {
			Assert.assertEquals("WebContainer", this.getXg().getSingle("Player[@id='-1']/@firstname"));
		}
	}
	
	@Test
	@JuTestEnv(systemProperties={
			"ju-testing-ee.selenium.driver=HtmlUnit,Chrome"
			, "ju-testing-ee.selenium.htmlUnit.enableJavascript=false"
	})
	public void canRun_containerTests_withMultipleDrivers() {
		JuAssumeUtils.chromeIsAvailable();
		
		EntityModificationTest.cnt = 0;
		EntityModificationTest.drivers = "";
		TestUtils.runJUnitTests(EntityModificationTest.class);
		Assert.assertEquals(2, EntityModificationTest.cnt);
		Assert.assertEquals("HtmlUnitDriverChromeDriver", EntityModificationTest.drivers);		
	}
	
	public static class EntityModificationTest extends WebContainerTest {
		private static int cnt;
		private static String drivers;
	
		@DataSet("WebContainerTestIT_onePlayer.xml")
		@Test
		public void changeFirstPlayerName() {
			SimpleJsfPage jsfPage = new SimpleJsfPage(this.driver);
			
			// First, the name should be TestIT as defined in the test data set
			Assert.assertEquals("TestIT", jsfPage.getFirstPlayerNameOnDb());
			
			// Now, set a new name
			jsfPage.setFirstPlayerName("NewName" + cnt);
			jsfPage.clickSave();
			Assert.assertEquals("NewName" + cnt, jsfPage.getFirstPlayerNameOnDb());
			
			logger.debug("FirstPlayerNameOnDb: " + jsfPage.getFirstPlayerNameOnDb());
			
			cnt++;
			drivers += this.driver.getClass().getSimpleName();
		}
	}
}
