package ch.inftec.ju.ee.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.inftec.ju.ee.client.ServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.ThreadUtils;

/**
 * Remote object that will be called ty the RemoteContainerTestIT.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
public class RemoteContainerTester {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected ServiceLocator serviceLocator = ServiceLocatorBuilder.buildLocal().createServiceLocator();
	
	private static final Map<Class<?>, Params> params = new HashMap<>();
	
//	public String getTestingEntityName(Long id) {
//		TestingEntity te = this.testingEntityRepo.findOne(id);
//		return te == null ? null : te.getName();
//	}
//	
//	public Long getTestingEntityId(String name) {
//		TestingEntity te = this.testingEntityRepo.getByName(name);
//		return te == null ? null : te.getId();
//	}
//	
//	public Long createTestingEntity(String name) {
//		return this.testingEntityRepo.save(new TestingEntity(name)).getId();
//	}
//	
//	public Long createTestingEntityAndWaitForSignal(String name, int signal) {
//		Long id = this.testingEntityRepo.save(new TestingEntity(name)).getId();
//		logger.info("Created entity {}, waiting for signal {}", id, signal);
//		this.waitForSignal(signal);
//		logger.info("Got signal for {}: {}", id, signal);
//		
//		return id;
//	}
//	
//	public Long createTestingEntityAndAfterWaitingWaitForSignal(String name, int waiting, int signal) {
//		this.waitForWaiting(waiting);
//		
//		// Make sure we don't see the first entity that is in a pending transaction
//		Assert.assertNull(this.testingEntityRepo.findOne(1L));
//		
//		Long id = this.testingEntityRepo.save(new TestingEntity(name)).getId();
//		logger.info("Created entity {}, waiting for signal {}", id, signal);
//		this.waitForSignal(signal);
//		logger.info("Got signal for {}: {}", id, signal);
//		
//		return id;
//	}
	
	public final void sendSignal(int signal) {
		logger.debug("Sending signal {}", signal);
		this.getParams().signals.add(signal);
	}
	
	public final void clearSignals() {
		logger.info("Clearing signals");
		this.getParams().signals.clear();
	}
	
	protected final void waitForSignal(int signal) {
		logger.debug("Waiting on signal {}", signal);
		
		int waitTime = 0;
		while (waitTime < this.getParams().maxWaitTime) {
			if (this.getParams().signals.contains(signal)) {
				logger.debug("Received signal {}", signal);
				return;
			}
			
			waitTime += this.getParams().pollingInterval;
			ThreadUtils.sleep(this.getParams().pollingInterval);
		}
		
		logger.error("Haven't received signal {}", signal);
		throw new JuRuntimeException("Haven't received signal");
	}
	
	private synchronized Params getParams() {
		if (!params.containsKey(this.getClass())) {
			params.put(this.getClass(), new Params());
		}
		return params.get(this.getClass()); 
	}
	
	private static class Params {
		private Set<Integer> signals = new HashSet<>();
		
		private int pollingInterval = 50; // 50 ms
		private int maxWaitTime = 60000; // 1 minute
	}
}
