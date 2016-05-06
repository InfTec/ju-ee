package ch.inftec.ee.bean;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import ch.inftec.ju.ee.test.ContainerTestScoped;

/**
 * Producer for container test scoped beans.
 * <p>
 * Added when experiencing problems with more than one container test scoped producer.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
public class ContainerTestScopeProducer {
	/**
	 * We cannot use String directly as it is final and thus not proxyable (required for non pseudo scopes)
	 * 
	 * @author martin.meyer@inftec.ch
	 *
	 */
	public static class MyString {
		private String s;

		MyString() {
			// Required to be proxied...
		}

		public MyString(String s) {
			this.s = s;
		}

		public String getValue() {
			return this.s;
		}
	}

	@Produces
	@Named("containerTestScoped_string1")
	@ContainerTestScoped
	public MyString createString1() {
		return new MyString("containerTestScoped_string1");
	}

	@Produces
	@Named("containerTestScoped_string2")
	@ContainerTestScoped
	public MyString createString2() {
		return new MyString("containerTestScoped_string2");
	}
}
