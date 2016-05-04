package ch.inftec.ju.ee.test;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.db.JuConnUtils;
import ch.inftec.ju.ee.client.ServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.ee.test.DbDataUtilConfigIT.MyDbDataUtilProvider;
import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.DbDataUtil;
import ch.inftec.ju.testing.db.DbDataUtilConfig;
import ch.inftec.ju.testing.db.DbDataUtilProvider;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;

@DbDataUtilConfig(MyDbDataUtilProvider.class)
public class DbDataUtilConfigIT extends ContainerTest {
	@Override 
	protected void doInit() {
		MyDbDataUtilProvider.calls = 0;
	}
	
	@Test
	@DataSet("DbDataUtilConfigIT_testingEntity1a.xml")
	@DataSetExport(tablesDataSet="DbDataUtilConfigIT_testingEntity1a.xml")
	@DataVerify
	public void dataUtilProvider_works_withoutModifications() {
		Assert.assertEquals("DbDataUtilConfigIT_1a", this.em.find(TestingEntity.class, 1L).getName());
	}
	public static class DataUtilProvider_works_withoutModifications extends DataVerifier {
		public void verify() throws Exception {
			// Make sure our provider was used
			Assert.assertEquals(2, MyDbDataUtilProvider.calls);
			
			Assert.assertEquals("DbDataUtilConfigIT_1a", this.getXg().getSingle("//TestingEntity/@name"));
		};
	}
	
	@Test
	@DataSet("DbDataUtilConfigIT_testingEntity1b.xml")
	@DataSetExport(tablesDataSet="DbDataUtilConfigIT_testingEntity1b.xml")
	@DataVerify
	public void dataUtilProvider_works_withModifications() {
		TestingEntity te = this.em.find(TestingEntity.class, 1L);
		te.setName("DbDataUtilConfigIT_1 modified");
	}
	public static class DataUtilProvider_works_withModifications extends DataVerifier {
		public void verify() throws Exception {
			// Make sure our provider was used
			Assert.assertEquals(2, MyDbDataUtilProvider.calls);
			
			Assert.assertEquals("DbDataUtilConfigIT_1 modified", this.getXg().getSingle("//TestingEntity/@name"));
		};
	}
	
	public static class MyDbDataUtilProvider implements DbDataUtilProvider {
		private static int calls = 0;
		private static DbDataUtil dbDataUtil;
		
		@Override
		public DbDataUtil getDbDataUtil() {
			if (dbDataUtil == null) {
				ServiceLocator sl = ServiceLocatorBuilder.buildLocal().createServiceLocator();
				DataSource ds = (DataSource) sl.lookup("jboss/datasources/ee-earDS");
				Assert.assertNotNull(ds);
				
				dbDataUtil = new DbDataUtil(JuConnUtils.createByDataSource(ds));
//				List<String> tableNames = JuConnUtils.createByDataSource(ds).getMetaDataInfo().getTableNames();
//				
//				dbDataUtil = new DbDataUtil(em);
			}
			
			calls++;
			return dbDataUtil;
		}
	}
}