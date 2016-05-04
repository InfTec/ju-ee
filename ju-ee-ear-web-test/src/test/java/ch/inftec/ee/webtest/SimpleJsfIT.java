package ch.inftec.ee.webtest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import ch.inftec.ju.ee.webtest.WebTestUtils;
import ch.inftec.ju.testing.db.JuAssumeUtils;

public class SimpleJsfIT {
	@Test
	@Ignore("Doesn't load test data")
	public void canOpen_simpelJsfPage_inChrome() {
		JuAssumeUtils.chromeIsAvailable();
		JuAssumeUtils.internetIsAvailable();
		
		// For more information about the Chrome driver, see https://code.google.com/p/selenium/wiki/ChromeDriver
		// One has to download the chromedriver.exe (which is platform dependent) to drive Chrome
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");

		WebDriver driver = new ChromeDriver();
		WebTestUtils.getPage(driver, "ee-ear-web/simpleJsf.jsf");
		
		// We don't test for an exact number as we don't to data set loading in this test case
		Assert.assertTrue(driver.findElement(By.id("playerCntEm")).getText().length() > 0);
		Assert.assertTrue(driver.findElement(By.id("playerCntRepo")).getText().length() > 0);
		
		driver.quit();
	}
	
	@Test
	@Ignore("Doesn't load test data")
	public void canOpen_simpelJsfPage_usingHtmlUnit() {
		WebDriver driver = new HtmlUnitDriver();
		WebTestUtils.getPage(driver, "ee-ear-web/simpleJsf.jsf");
		
		// We don't test for an exact number as we don't to data set loading in this test case
		Assert.assertTrue(driver.findElement(By.id("playerCntEm")).getText().length() > 0);
		Assert.assertTrue(driver.findElement(By.id("playerCntRepo")).getText().length() > 0);
		
		driver.quit();
	}
}
