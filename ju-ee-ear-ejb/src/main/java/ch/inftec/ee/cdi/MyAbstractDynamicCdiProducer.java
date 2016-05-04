package ch.inftec.ee.cdi;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import ch.inftec.ju.ee.cdi.DynamicCdi;
import ch.inftec.ju.ee.cdi.DynamicCdiTag;

public class MyAbstractDynamicCdiProducer {
	@Produces
	public DynamicCdi createDefaultScopeCdi() {
		return new FactoryAbstractDynamicCdi();
	}
	
	/*
	 * Will not be picked up by @Any Instance<>
	 */
	@Produces @RequestScoped
	public DynamicCdi createRequestScopeCdi() {
		return new RequestAbstractDynamicCdi();
	}
	
	@Produces
	public DynamicCdi createRequest2ScopeCdi() {
		return new Request2AbstractDynamicCdi();
	}
	
	@DynamicCdiTag("factory")
	private class FactoryAbstractDynamicCdi extends MyAbstractDynamicCdi {
		@Override
		public String getType() {
			return "factory";
		}
	}
	
	@DynamicCdiTag("request")
	private class RequestAbstractDynamicCdi extends MyAbstractDynamicCdi {
		@Override
		public String getType() {
			return "request";
		}
	}
	
	@DynamicCdiTag("request2")
	private class Request2AbstractDynamicCdi extends MyAbstractDynamicCdi {
		@Override
		public String getType() {
			return "request2";
		}
	}
}
