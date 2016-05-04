package ch.inftec.ee.cdi;

import ch.inftec.ju.ee.cdi.DynamicCdiFactory;
import ch.inftec.ju.ee.cdi.DynamicCdiTag;

public class MyDynamicCdiFactory implements DynamicCdiFactory {
	@DynamicCdiTag
	public MyFactoryCdi createDynamicCdiFromFactory() {
		return new MyFactoryCdi() {
			@Override
			public String type() {
				return "factoryDefault";
			}
		};
	}
	
	@DynamicCdiTag("factoryAlt")
	public MyFactoryCdi createDynamicCdiFromFactoryAlt() {
		return new MyFactoryCdi() {
			@Override
			public String type() {
				return "factoryAlt";
			}
		};
	}
}
