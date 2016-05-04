package ch.inftec.ee.webtest;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Create an Arquillian micro deployment (war) and access it using Arquillian Drone,
 * backed by Selenium WebDriver.
 * @author Martin
 *
 */
@RunWith(Arquillian.class)
public class DroneTest {
	@ArquillianResource
	URL contextPath;
	
	@Drone
	WebDriver driver;
	
	@Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
        	.addAsWebResource(new File("src/test/resources/drone/staticHtml.html"));
    }
	
	@Test
	public void staticHtml_isAccessible_byDrone() {
		driver.get(contextPath + "staticHtml.html");
		Assert.assertEquals("Static HTML", driver.getTitle());
	}
}
