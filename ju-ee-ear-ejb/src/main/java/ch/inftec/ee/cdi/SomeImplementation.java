package ch.inftec.ee.cdi;

/**
 * Class to test CDI lookup for implemention of an interface.
 * @author martin.meyer@inftec.ch
 *
 */
public class SomeImplementation implements SomeInterface {
	@Override
	public String getValue() {
		return "Some Implemenation";
	}
}
