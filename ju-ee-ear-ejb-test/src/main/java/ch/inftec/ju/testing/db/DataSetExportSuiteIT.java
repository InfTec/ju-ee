package ch.inftec.ju.testing.db;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.DataSetExportSuite.RootSuite;
import ch.inftec.ju.testing.db.DataSetExportSuiteIT.MyTestExportsSuite;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;

/**
 * Test to make sure that ContainerTests are working when referenced by a DataSetExportSuite.
 * 
 * @author martin.meyer@inftec.ch
 * 
 */
@RunWith(DataSetExportSuite.class)
@RootSuite(MyTestExportsSuite.class)
public class DataSetExportSuiteIT {
	@RunWith(Suite.class)
	@SuiteClasses({ _exports_TestClass.class })
	public static class MyTestExportsSuite {
	}

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static class _exports_TestClass extends ContainerTest {
		@Test
		@DataSet("ju-testing/data/default-fullData.xml")
		public void canRun_containerTest_usingDataSetExportSuite() {
			TestingEntityRepo repo = this.serviceLocator.cdi(TestingEntityRepo.class);
			TestingEntity te = repo.findOne(-1L);
			Assert.assertEquals("Test1", te.getName());
		}
	}
}
