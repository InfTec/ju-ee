package ch.inftec.ee.cdi;

import ch.inftec.ju.ee.cdi.DynamicCdi;
import ch.inftec.ju.ee.cdi.DynamicCdiTag;

@DynamicCdiTag("request")
public class MyScopeTest2 extends MyScopeTest implements DynamicCdi {
	@Override
	public String type() {
		return "request";
	}
}
