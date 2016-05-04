package ch.inftec.ee.test.db;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.DbDataUtil;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;
import ch.inftec.ju.util.xml.XPathGetter;

public class DbDataUtilIT extends ContainerTest {
	@Test
	public void canImportData_fromDatasetFile() {
		DbDataUtil du = new DbDataUtil(this.em);
		du.cleanImport("/ee-ear-ejb-test/db/DbDataUtilIT_testingEntity.xml");
		TestingEntity te = this.em.find(TestingEntity.class, 1L);
		Assert.assertEquals("DbDataUtilIT", te.getName());
	}
	
	@Test
	public void canExportData_toXmlDocument() {
		DbDataUtil du = new DbDataUtil(this.em);
		du.buildImport().from("/ee-ear-ejb-test/db/DbDataUtilIT_testingEntity.xml").executeDeleteAll();
		this.emUtil.resetIdentityGenerationOrSequences(1);
		
		TestingEntity te = new TestingEntity();
		te.setName("Export Test");
		this.em.persist(te);
		this.em.flush(); // Not all DBs require a flush here, but it's safer
		
		Document doc = du.buildExport().addTable("TestingEntity").writeToXmlDocument();
		XPathGetter xg = new XPathGetter(doc);
		Assert.assertEquals(1, xg.getArray("//TestingEntity").length);
		Assert.assertEquals("Export Test", xg.getSingle("//TestingEntity/@name"));
	}
}
