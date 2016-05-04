package ch.inftec.ju.ee.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetConfig;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;
import ch.inftec.ju.util.IOUtil;

/**
 * Tests data set verifying. Unfortunately, we don't have a negative test case,
 * i.e. a test case where verifying fails as we couldn't have the test succeed
 * on failure.
 * @author Martin
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DataSetExport(tablesDataSet="ch/inftec/ju/ee/test/DataSetVerifyIT_testingEntity1.xml")
public class DataSetVerifyIT extends ContainerTest {
	@Test
	@DataSet("DataSetVerifyIT_testingEntity1.xml")
	@JuTestEnv(systemProperties = {"ju-testing.export.compareToResource=false"})
	public void _01_dataSet_isWrittenToFile_whenCompareToResource_isFalse() {
		Path p = this.getLocalPath(
				"src/main/resources/dataSetExport/DataSetVerifyIT_dataSet_isWrittenToFile_whenCompareToResource_isFalse.xml", false);
		IOUtil.deleteFile(p);
		
		this.em.find(TestingEntity.class, 1L).setName("DataSet_WriteToFile");
		
		// Writing is verified in _02_dataSet_isWrittenToFile_whenCompareToResource_isFalse
	}
	
	@Test
	@JuTestEnv(systemProperties = {"ju-testing.export.compareToResource=false"})
	public void _02_dataSet_isWrittenToFile_whenCompareToResource_isFalse() {
		// Verifies _01_dataSet_isWrittenToFile_whenCompareToResource_isFalse
		
		// Make sure the file exists
		Path p = this.getLocalPath(
				"src/main/resources/dataSetExport/DataSetVerifyIT_dataSet_isWrittenToFile_whenCompareToResource_isFalse.xml", false);
		Assert.assertTrue(Files.exists(p));
		Assert.assertTrue(Files.isRegularFile(p));
	}
		
	@Test
	@DataSet("DataSetVerifyIT_testingEntity1.xml")
	public void data_isVerified_againstDataSet() {
		TestingEntityRepo teRepo = this.serviceLocator.cdi(TestingEntityRepo.class);
		teRepo.findOne(1L).setName("DataSetVerifyIT1_verify");
	}
	
	@Test
	@DataSet("DataSetVerifyIT_testingEntity1.xml")
	@DataVerify
	public void canVerifyData_usingXmlDocument_whenExportingDataSet() {
		
	}
	public static class CanVerifyData_usingXmlDocument_whenExportingDataSet extends DataVerifier {
		public void verify() throws Exception {
			String name = this.getXg().getSingle("/dataset/TestingEntity[@id='1']/@name");
			Assert.assertEquals("DataSetVerifyIT1", name);
		};
	}
	
	@Test
	@DataSet("DataSetVerifyIT_testingEntity1.xml")
	@DataVerify
	public void canVerifyData_usingXmlDocument_whenExportingDataSet_withoutDataSetPrefix() {
		
	}
	public static class CanVerifyData_usingXmlDocument_whenExportingDataSet_withoutDataSetPrefix extends DataVerifier {
		public void verify() throws Exception {
			String name = this.getXg().getSingle("TestingEntity[@id='1']/@name");
			Assert.assertEquals("DataSetVerifyIT1", name);
		};
	}
	
	@Test
	@DataSet(value="ju-testing/data/default-noData.xml"
			, inserts={"DataSetVerifyIT_testingEntity1.xml"})
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml")
	@DataVerify
	public void canVerifyRowCount_usingXmlDocument_whenExportingDataSet() {
		
	}
	public static class CanVerifyRowCount_usingXmlDocument_whenExportingDataSet extends DataVerifier {
		public void verify() throws Exception {
			Assert.assertEquals(1, this.getXg().getCount("TestingEntity[@id]"));
			Assert.assertFalse(this.getXg().isEmptyElement("TestingEntity"));
			
			Assert.assertEquals(0, this.getXg().getCount("Player[@id]"));
			Assert.assertEquals(1, this.getXg().getCount("Player"));
			
			Assert.assertTrue(this.getXg().isEmptyElement("Player"));
		};
	}
	
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml"
			, exportName = "DataSetExportIT_customNameAndTargetDir.xml")
	@DataSetConfig(resourceDir = "src/main/resources", resourcePrefix = "dataSetExport/custTarget")
	@JuTestEnv(systemProperties = {"ju-testing.export.compareToResource=true"})
	public void exportCanBeVerified_inNonStandardTargetDir() {
		TestingEntityRepo teRepo = this.serviceLocator.cdi(TestingEntityRepo.class);
		TestingEntity te = new TestingEntity("exportCanBeVerified_inNonStandardTargetDir");
		teRepo.save(te);
	}
}
