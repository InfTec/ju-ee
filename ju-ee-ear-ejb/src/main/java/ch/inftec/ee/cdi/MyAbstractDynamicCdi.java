package ch.inftec.ee.cdi;

import ch.inftec.ju.ee.cdi.DynamicCdi;

public abstract class MyAbstractDynamicCdi extends AbstractCdi implements DynamicCdi {
	public String getType() {
		return "base";
	}
}
