package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class smallVersionTest extends ContainerTest {
	@Test
	public void versionTest(){
		
		TestRunnerFacade facade = new RestTestRunnerFacade();
		String version = facade.getVersion();
		
		assertThat(version,is("1"));
	}
	
	@Test
	public void simpleTest(){
		
		System.out.println("this is a very simple test");
	}
}
