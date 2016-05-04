package ch.inftec.ee.webtest;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.inftec.ju.testing.db.JuAssumeUtils;

public class SeleniumTest {
	@Test
	@Ignore
	public void canOpenGoogle_andSearchForCheese_usingFirefox() {
		 // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        this.googleForCheese(driver);
	}
	
	@Test
	public void canOpenGoogle_andSearchForCheese_usingChrome() {
		JuAssumeUtils.chromeIsAvailable();
		JuAssumeUtils.internetIsAvailable();
		
		// For more information about the Chrome driver, see https://code.google.com/p/selenium/wiki/ChromeDriver
		// One has to download the chromedriver.exe (which is platform dependent) to drive Chrome
//		System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        this.googleForCheese(driver);
	}
	
	@Test
	public void canOpenGoogle_andSearchForCheese_usingHtmlUnit() {
		JuAssumeUtils.internetIsAvailable();
		
		 // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
//		System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
		HtmlUnitDriver driver = new HtmlUnitDriver();
//		driver.setJavascriptEnabled(true);

        this.googleForCheese(driver);
	}
	
	private void googleForCheese(WebDriver driver) {
		// And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!"); // 

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese"); //Somehow, Chrome doesn't receive the '!'...
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();
	}
}
