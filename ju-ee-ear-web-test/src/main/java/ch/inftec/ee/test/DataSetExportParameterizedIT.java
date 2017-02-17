package ch.inftec.ee.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.inftec.ju.ee.test.WebContainerTest;
import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;

/**
 * Tests data set exporting using Parameterized runner.
 * @author Martin
 *
 */
@Ignore("TODO: Fix for REST")
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DataSetExport(tablesDataSet="DataSetExportParameterized_player1.xml")
public class DataSetExportParameterizedIT extends WebContainerTest {
	
	@Parameters
	public static Iterable<String[]> params() {
		return Arrays.asList(new String[][] {{"name1"}, {"name2"}});
	}
	
	private final String name;
	
	public DataSetExportParameterizedIT(String name) {
		this.name = name;
	}
	
	@Test
	@DataSet("DataSetExportParameterized_player1.xml")
	public void _01_parameterizedExport() {
		new SimpleJsfPage(this.driver)
				.setFirstPlayerName(this.name)
				.clickSave();
	}
	
	@Test
	@DataSet(value = "dataSetExport/DataSetExportParameterizedIT_parameterizedExport{param}.xml")
	public void _02_parameterizedExport() {
		String nameOnDb = new SimpleJsfPage(this.driver)
				.getFirstPlayerNameOnDb();
		
		Assert.assertEquals(this.name, nameOnDb);
	}
}