package ch.inftec.ee.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ch.inftec.ju.ee.webtest.SeleniumPage;

public class SimpleJsfPage extends SeleniumPage {
	public SimpleJsfPage(WebDriver driver) {
		super(driver);
		
		this.getPage("ju-ee-ear-web/simpleJsf.jsf");
	}
	
	public SimpleJsfPage setFirstPlayerName(String name) {
		this.driver.findElement(By.id("playerFrm:firstPlayerName")).sendKeys(name);
		return this;
	}
	
	public SimpleJsfPage clickSave() {
		this.driver.findElement(By.id("playerFrm:btnSave")).click();
		return this;
	}
	
	public String getFirstPlayerNameOnDb() {
		return this.driver.findElement(By.id("firstPlayerNameOnDb")).getText();
	}
}
