package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.ServerCode;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;
import ch.inftec.ju.util.JuRuntimeException;
import ch.inftec.ju.util.TestUtils;

public class ContainerTestIT extends ContainerTest {
	@Test
	public void containerTest_initializes_serviceLocator() {
		Assert.assertNotNull(this.serviceLocator);
	}
	
	@Test
	public void canLookup_cdi_usingServiceLocator() {
		Greeter greeter = this.serviceLocator.cdi(Greeter.class);
		Assert.assertEquals("Hello, ServiceLocator!", greeter.createGreeting("ServiceLocator"));
	}
	
	@Test
	@DataSet("ContainerTestIT_testingEntity1.xml")
	public void loadsDataSet_usingRelativePath() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.findOne(1L);
		Assert.assertEquals("ContainerTestIT1", te.getName());
	}
	
	@Test
	@DataSet("ee-ear-ejb-test/db/ContainerTestIT_testingEntity2.xml")
	public void loadsDataSet_usingAbsolutePath() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.findOne(2L);
		Assert.assertEquals("ContainerTestIT2", te.getName());
	}
	
	@Test
	@DataSet(value="ContainerTestIT_testingEntity1.xml"
		, inserts = {"ee-ear-ejb-test/db/ContainerTestIT_testingEntity2.xml"})
	public void loadsInsertDataSets() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		Assert.assertEquals("ContainerTestIT1", repo.findOne(1L).getName());
		Assert.assertEquals("ContainerTestIT2", repo.findOne(2L).getName());
	}
	
	@Test
	@DataSet("ee-ear-ejb-test/db/ContainerTestIT_testingEntity2.xml")
	public void dataSetLoading_resetsSequencesTo1_byDefault() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.save(new TestingEntity());
		Assert.assertEquals(new Long(1L), te.getId());
	}
	
	@Test
	@DataSet(value = "ee-ear-ejb-test/db/ContainerTestIT_testingEntity2.xml"
		, sequenceValue = 3)
	public void dataSetLoading_resetsSequencesToSpecifiedValue() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.save(new TestingEntity());
		Assert.assertEquals(new Long(3L), te.getId());
	}
	
	@Test
	@DataSet("ContainerTestIT_testingEntity1.xml")
	@DataVerify()
	public void transaction_isCommitted_ifNoException() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.findOne(1L);
		te.setName("Changed name...");
		
		Assert.assertEquals("Changed name...", repo.findOne(1L).getName());
	}
	public static class Transaction_isCommitted_ifNoException extends DataVerifierCdi {
		@Override
		public void verify() {
			TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
			Assert.assertEquals("Changed name...", repo.findOne(1L).getName());
		}
	}
	
	@Test(expected=JuRuntimeException.class)
	@DataSet("ContainerTestIT_testingEntity1.xml")
	@DataVerify()
	public void transaction_isRolledBack_ifException() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.findOne(1L);
		te.setName("Changed name...");
		
		throw new JuRuntimeException("Exception...");
	}
	public static class Transaction_isRolledBack_ifException extends DataVerifierCdi {
		@Override
		public void verify() {
			TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
			Assert.assertEquals("ContainerTestIT1", repo.findOne(1L).getName());
		}
	}
	
	@Test
	@DataSet("ContainerTestIT_testingEntity1.xml")
	@DataVerify()
	public void transaction_canBeController_usingTxHandler() {
		TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = repo.findOne(1L);
		te.setName("First Change...");
		getTransactionHandler().commitAndStartNewTransaction();
		
		te = repo.findOne(1L);
		te.setName("Second Change...");
		getTransactionHandler().commitAndStartNewTransaction();
		
		te = repo.findOne(1L);
		te.setName("Third Change...");
		getTransactionHandler().rollbackIfNotCommittedWithoutStartingNewTransaction();
	}
	public static class Transaction_canBeController_usingTxHandler extends DataVerifierCdi {
		@Override
		public void verify() {
			TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
			Assert.assertEquals("Second Change...", repo.findOne(1L).getName());
		}
	}
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	public void dataSetLoading_resetsSequences1() {
		this.testSequencesReset();
	}
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	public void dataSetLoading_resetsSequences2() {
		this.testSequencesReset();
	}
	
	private void testSequencesReset() {
		TestingEntity te = new TestingEntity();
		this.em.persist(te);
		Assert.assertEquals(new Long(1L), te.getId());
	}

	@Test
	@DataSet(value = DataSet.NO_CLEAN_INSERT
			, preInitializer = DataSet_PreInitializer_isCalled.class)
	public void dataSet_preInitializer_isCalled() {
		TestingEntityRepo teRepo = this.emUtil.getJpaRepository(TestingEntityRepo.class);
		Assert.assertEquals("PreInitializer", teRepo.findAll().get(0).getName());
	}
	public static class DataSet_PreInitializer_isCalled extends ServerCode {
		@Override
		public void execute() throws Exception {
			TestingEntityRepo teRepo = this.emUtil.getJpaRepository(TestingEntityRepo.class);
			teRepo.deleteAll();
			
			TestingEntity te = new TestingEntity();
			te.setName("PreInitializer");
			teRepo.save(te);
		}
	}
	
	@Test
	@DataSet(value = "ju-testing/data/default-noData.xml"
			, postInitializer = DataSet_PostInitializer_isCalled.class)
	public void dataSet_postInitializer_isCalled() {
		TestingEntityRepo teRepo = this.emUtil.getJpaRepository(TestingEntityRepo.class);
		Assert.assertEquals("PostInitializer", teRepo.findAll().get(0).getName());
	}
	public static class DataSet_PostInitializer_isCalled extends ServerCode {
		@Override
		public void execute() throws Exception {
			TestingEntityRepo teRepo = this.emUtil.getJpaRepository(TestingEntityRepo.class);
			teRepo.deleteAll();
			
			TestingEntity te = new TestingEntity();
			te.setName("PostInitializer");
			teRepo.save(te);
		}
	}
	
	@Test
	public void supports_assume() {
		Assume.assumeFalse(true);
	}
	
	@Test
	@Ignore("Not working...")
	public void canRun_nestedClass_containerTests() {
		TestUtils.runJUnitTests(NestedTest.class);
	}
	
	public static class NestedTest extends ContainerTest {
		@Test
		public void test() {
		}
	}
}