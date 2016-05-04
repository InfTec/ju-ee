package ch.inftec.ee.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.test.Greeter;

@Ignore(value="Needs correct management port in arquillian.xml configuration file")
@RunWith(Arquillian.class)
public class ArquillianRemoteIT {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
        		.addClasses(Greeter.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    Greeter greeter;

    @Test
    public void greeter_greets() throws Exception {
        Assert.assertEquals("Hello, World!", greeter.createGreeting("World"));
    }
    
    @Test
    @Ignore // Just for failing tests
    public void fail() {
    	Assert.fail("Failing integration test");
    }
}