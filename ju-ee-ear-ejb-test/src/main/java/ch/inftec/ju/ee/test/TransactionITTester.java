package ch.inftec.ju.ee.test;

import javax.persistence.EntityManager;

import org.junit.Assert;

import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.util.JuRuntimeException;

/**
 * Remote object that will be called ty the RemoteContainerTestIT.
 * @author martin.meyer@inftec.ch
 *
 */
public class TransactionITTester extends RemoteContainerTestITTester {
	public Long getTestingEntityId(String name) {
		TestingEntity te = this.testingEntityRepo.getByName(name);
		return te == null ? null : te.getId();
	}
	
	public String getTestingEntityName(Long id) {
		TestingEntity te = this.testingEntityRepo.findOne(id);
		return te == null ? null : te.getName();
	}
	
	public Long transaction1() {
		return this.transaction1(false);
	}
	
	public void transaction1_withRollback() {
		this.transaction1(true);
	}
	
	private Long transaction1(boolean rollback) {
		this.sendSignal(1);
		
		// Wait until the transaction2 is running...
		this.waitForSignal(2);
		
		// Create object
		Long id = this.testingEntityRepo.save(new TestingEntity("T1")).getId();
//		this.serviceLocator.cdi(EntityManager.class).flush(); If we flush, we get a lock table exception: 14:03:04,989 ERROR [org.hibernate.engine.jdbc.spi.SqlExceptionHelper] (EJB default - 9) Timeout trying to lock table "TESTINGENTITY"; SQL statement:
		this.sendSignal(3);
		this.waitForSignal(4);
		
		if (rollback) {
			logger.info("Rolling back: {}", id);
			throw new JuRuntimeException("Rolling back: " + id);
		} else {
			return id;
		}
	}
	
	public Long transaction2() {
		return this.transaction2(false);
	}
	
	public Long transaction2_withRollback() {
		return this.transaction2(true);
	}
	
	private Long transaction2(boolean rollback) {
		// Wait for transaction1 to be present
		this.waitForSignal(1);
		
		// Check of the object transaction1 is creating before it is actually created
		Assert.assertFalse(this.testingEntityRepo.exists(1L));
		this.sendSignal(2);
		
		// Wait for the object to be created in transaction1
		this.waitForSignal(3);
		
		// Make sure it is still not found
		Assert.assertFalse(this.testingEntityRepo.exists(1L));
		this.sendSignal(4);
		
		// Wait for the transaction1 to be finished
		this.waitForSignal(5);
		
		// Make sure the object still exists, unless the transaction was rolled back
		Assert.assertEquals(!rollback, this.testingEntityRepo.exists(1L));
		
		// Create an object in this transaction
		Long id = this.testingEntityRepo.save(new TestingEntity("T2")).getId();
		return id;
	}
}
