package ch.inftec.ee.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.inftec.ju.ee.test.WebContainerTest;
import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.JuTestEnv;
import ch.inftec.ju.testing.db.data.entity.Player;
import ch.inftec.ju.util.IOUtil;
import ch.inftec.ju.util.TestUtils;

public class ParameterizedWebContainerTestIT extends WebContainerTest {
	@DataSet("ParameterizedWebContainerTestIT_onePlayer.xml")
	@Test
	@JuTestEnv(systemProperties = {"ju-testing.export.compareToResource=false"})
	public void parameterizedWebContainerTest_isSupported() throws Exception {
		// Clear test fixture
		ParameterizedTest.params.clear();
		Path ex0 = Paths.get("src/main/resources/dataSetExport/ParameterizedTest_modifyFirstPlayerName[0].xml");
		Path ex1 = Paths.get("src/main/resources/dataSetExport/ParameterizedTest_modifyFirstPlayerName[1].xml");
		IOUtil.deleteFiles(ex0, ex1);
		
		TestUtils.runJUnitTests(ParameterizedTest.class);
		TestUtils.assertCollectionConsistsOfAll(ParameterizedTest.params, "s1", "s2");
		
		Assert.assertEquals(2, ParameterizedTest.params.size());
		
		// Verify export files
		TestUtils.assertEqualsResource("ParameterizedTest_modifyFirstPlayerName[0].xml"
				, new IOUtil().loadTextFromUrl(ex0.toUri().toURL()));
		TestUtils.assertEqualsResource("ParameterizedTest_modifyFirstPlayerName[1].xml"
				, new IOUtil().loadTextFromUrl(ex1.toUri().toURL()));
	}
	public static final class ParameterizedWebContainerTest_isSupported extends DataVerifier {
		public void verify() throws Exception {
			// Verify data
			Player p = this.em.find(Player.class, -1L);
			Assert.assertEquals("WCs1s2", p.getLastName());
		};
	}
	
	@RunWith(Parameterized.class)
	public static class ParameterizedTest extends WebContainerTest {
		private static List<String> params = new ArrayList<>();

		@Parameters
		public static Iterable<String[]> params() {
			return Arrays.asList(new String[][] {{"s1"}, {"s2"}});
		}
		
		private final String param;
		
		public ParameterizedTest(String param) {
			this.param = param;
		}
		
		@Test
		@DataSetExport(tablesDataSet="ParameterizedWebContainerTestIT_onePlayer.xml")
		public void modifyFirstPlayerName() {
			SimpleJsfPage jsfPage = new SimpleJsfPage(this.driver);
			
			// First, the name should be TestIT as defined in the test data set
			Assert.assertTrue(jsfPage.getFirstPlayerNameOnDb().startsWith("WC"));
			
			// Now, set a new name
			jsfPage.setFirstPlayerName(jsfPage.getFirstPlayerNameOnDb() + param);
			jsfPage.clickSave();
			
			logger.debug("FirstPlayerNameOnDb: " + jsfPage.getFirstPlayerNameOnDb());
			
			params.add(param);
		}
	}
}
