package ch.inftec.ju.ee.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.testing.db.DataSetExport.ExportType;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;
import ch.inftec.ju.util.IOUtil;
import ch.inftec.ju.util.JuException;
import ch.inftec.ju.util.JuUrl;
import ch.inftec.ju.util.TestUtils;

/**
 * Tests data set exporting.
 * @author Martin
 *
 */
@DataSetExport(tablesDataSet = "ju-testing/data/default-noData.xml")
@JuTestEnv(systemProperties = {"ju-testing.export.compareToResource=false"})
public class DataSetExportIT extends ContainerTest {
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataVerify
	public void data_isExported() {
		Path exportFile = this.getLocalPath("src/main/resources/dataSetExport/DataSetExportIT_data_isExported.xml", true);
		IOUtil.deleteFile(exportFile);

		// We set the name of the file we expect the data to be exported in the testing entity
		// This way, we can access it from the data verifier
		TestingEntity te = new TestingEntity();
		te.setName(exportFile.toAbsolutePath().toString());
		this.serviceLocator.cdi(TestingEntityRepo.class).save(te);
	}
	public static class Data_isExported extends DataVerifierCdi {
		@Override
		public void verify() throws JuException {
			String path = this.serviceLocator.cdi(TestingEntityRepo.class).findOne(1L).getName();
			String exportedXml = new IOUtil().loadTextFromUrl(JuUrl.toUrl(JuUrl.existingFile(path)));
			
			TestUtils.assertEqualsResource("DataSetExportIT_data_isExported.xml"
					, exportedXml
					, "path", path);
			
			IOUtil.deleteFile(JuUrl.existingFile(path));
		}
	}
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataVerify
	public void _01_leadingUnderscoreAndNumbers_areStripped() {
		Path exportFile = this.getLocalPath("src/main/resources/dataSetExport/DataSetExportIT_leadingUnderscoreAndNumbers_areStripped.xml", true);
		IOUtil.deleteFile(exportFile);

		// We set the name of the file we expect the data to be exported in the testing entity
		// This way, we can access it from the data verifier
		TestingEntity te = new TestingEntity();
		te.setName(exportFile.toAbsolutePath().toString());
		this.serviceLocator.cdi(TestingEntityRepo.class).save(te);
	}
	public static class LeadingUnderscoreAndNumbers_areStripped extends DataVerifierCdi {
		@Override
		public void verify() throws JuException {
			String path = this.serviceLocator.cdi(TestingEntityRepo.class).findOne(1L).getName();
			String exportedXml = new IOUtil().loadTextFromUrl(JuUrl.toUrl(JuUrl.existingFile(path)));
			
			TestUtils.assertEqualsResource("DataSetExportIT_data_isExported.xml"
					, exportedXml
					, "path", path);
			
			IOUtil.deleteFile(JuUrl.existingFile(path));
		}
	}
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataVerify
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml"
			, exportName="DataSetExportIT_customName.xml")
	public void exportName_canBeSpecified() {
		Path exportFile = this.getLocalPath("src/main/resources/dataSetExport/DataSetExportIT_customName.xml", true);
		IOUtil.deleteFile(exportFile);

		// We set the name of the file we expect the data to be exported in the testing entity
		// This way, we can access it from the data verifier
		TestingEntity te = new TestingEntity();
		te.setName(exportFile.toAbsolutePath().toString());
		this.serviceLocator.cdi(TestingEntityRepo.class).save(te);
	}
	public static class ExportName_canBeSpecified extends DataVerifierCdi {
		@Override
		public void verify() throws JuException {
			String path = this.serviceLocator.cdi(TestingEntityRepo.class).findOne(1L).getName();
			Assert.assertTrue(Files.exists(Paths.get(path)));
			
			IOUtil.deleteFile(JuUrl.existingFile(path));
		}
	}
	
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataVerify
	@DataSetExport(tablesDataSet="", exportType=ExportType.NONE)
	public void export_canBeDisabled_withTypeNone() {
		// Don't do anything
	}
	public static class Export_canBeDisabled_withTypeNone extends DataVerifierCdi {
		@Override
		public void verify() throws JuException {
			// We just check if we have an XPathGetter. We could also look for an export file...
			Assert.assertNull(this.getXg());
		}
	}
}
