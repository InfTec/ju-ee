package ch.inftec.ju.ee.test;

import org.junit.Test;

import ch.inftec.ee.TestFacade;

public class ContainerTestManualTests extends ContainerTest {
	@Test
	public void givenRuntimeExceptionInFacade_exceptionIsDisplayedCorrectly() {
		TestFacade testFacade = serviceLocator.cdi(TestFacade.class);
		testFacade.throwRuntimeException("Hello World");
	}
	
	@Test
	public void givenUncheckedExceptionInFacade_exceptionIsDisplayedCorrectly() {
		TestFacade testFacade = serviceLocator.cdi(TestFacade.class);
		testFacade.throwUncheckedException("Hello World");
	}
	
	@Test
	public void givenExceptionInFacade_exceptionIsDisplayedCorrectly() throws Exception {
		TestFacade testFacade = serviceLocator.cdi(TestFacade.class);
		testFacade.throwException("Hello World");
	}
}