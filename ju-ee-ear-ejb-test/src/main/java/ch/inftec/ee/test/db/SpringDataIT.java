package ch.inftec.ee.test.db;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.ContainerTest;
import ch.inftec.ju.testing.db.DbDataUtil;
import ch.inftec.ju.testing.db.data.repo.TestingEntityRepo;

public class SpringDataIT extends ContainerTest {
	@Test
	public void canLookup_springDataRepository() {
		DbDataUtil du = new DbDataUtil(this.em);
		du.cleanImport("/ee-ear-ejb-test/db/DbDataUtilIT_testingEntity.xml");
		
		TestingEntityRepo teRepo = this.serviceLocator.cdi(TestingEntityRepo.class);
		Assert.assertEquals(1, teRepo.count());
		Assert.assertEquals("DbDataUtilIT", teRepo.findOne(1L).getName());
	}
}