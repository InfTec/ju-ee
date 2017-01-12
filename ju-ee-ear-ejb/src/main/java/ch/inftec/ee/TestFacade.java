package ch.inftec.ee;

import javax.ejb.Stateless;

@Stateless
public class TestFacade {
	public void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}
	
	public void throwUncheckedException(String message) {
		throw new UncheckedException(message);
	}
	
	public void throwException(String message) throws Exception {
		throw new Exception(message);
	}
}
