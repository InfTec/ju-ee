package ch.inftec.ju.ee.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;

/**
 * Remote object that will be called ty the RemoteContainerTestIT.
 * @author martin.meyer@inftec.ch
 *
 */
public class RemoteContainerTestITTester extends RemoteContainerTester {
	protected TestingEntityRepo testingEntityRepo;
	
	public RemoteContainerTestITTester() {
		this.testingEntityRepo = this. serviceLocator.cdi(TestingEntityRepo.class);
	}
	
	public String getTestingEntityName(Long id) {
		TestingEntity te = this.testingEntityRepo.findOne(id);
		return te == null ? null : te.getName();
	}
	
	public String getConstValue() {
		return "Some Const Value";
	}
	
	public void getVoid() {
	}
	
	public static class ComplexParam {
		Map<String, String> map = new HashMap<>();
		List<Long> longList = new ArrayList<>();
	}
	
	public static class ComplexResult {
		Long longVal;
		String stringVal;
		ComplexParam complexParam;
	}
	
	public ComplexResult getComplexResult(Long longVal, String stringVal, ComplexParam complexParam) {
		ComplexResult res = new ComplexResult();
		
		res.longVal = longVal;
		res.stringVal = stringVal;
		res.complexParam = complexParam;
		
		return res;
	}
	
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
	
}
