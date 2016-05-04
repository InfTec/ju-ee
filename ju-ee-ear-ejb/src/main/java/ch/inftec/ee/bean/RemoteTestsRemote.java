package ch.inftec.ee.bean;

import javax.ejb.Remote;

@Remote
public interface RemoteTestsRemote {
	/**
	 * Returns a Hello [name] message
	 * 
	 * @param name
	 * @return
	 */
	String greet(String name);

	/**
	 * Makes sure the ContainerTestScope is not active if there is no container test.
	 * 
	 * @return
	 */
	boolean containerTestScopeIsNotActive();

	/**
	 * Gets the default scoped request.
	 * 
	 * @return Size of int requests. Every call adds one to the holder.
	 */
	int getScopedControlledRequest(String expectedString);
}
