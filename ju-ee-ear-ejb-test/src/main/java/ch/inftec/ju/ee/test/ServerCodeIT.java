package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;
import ch.inftec.ju.testing.db.PostServerCode;
import ch.inftec.ju.testing.db.ServerCode;
import ch.inftec.ju.testing.db.data.entity.TestingEntity;

public class ServerCodeIT extends ContainerTest {
	@Test
	@DataSet("ServerCodeIT_testingEntity1.xml")
	@PostServerCode(ServerCode_explicit.class)
	@DataVerify
	public void serverCode_isExecuted_whenSpecifiedExplicitly() {
	}
	public static final class ServerCode_explicit extends ServerCode {
		@Override
		public void execute() throws Exception {
			TestingEntity te = this.em.find(TestingEntity.class, 1L);
			te.setName("ServerCodeExplicit");
		}		
	}
	public static final class ServerCode_isExecuted_whenSpecifiedExplicitly extends DataVerifier {
		@Override
		public void verify() throws Exception {
			Assert.assertEquals("ServerCodeExplicit", this.em.find(TestingEntity.class, 1L).getName());
		}
	}
	
	@Test
	@DataSet("ServerCodeIT_testingEntity1.xml")
	@PostServerCode
	@DataVerify
	public void serverCode_isExecuted_withImplicitName() {
	}
	public static final class ServerCode_isExecuted_withImplicitName_code extends ServerCode {
		@Override
		public void execute() throws Exception {
			TestingEntity te = this.em.find(TestingEntity.class, 1L);
			te.setName("ServerCodeImplicit");
		}		
	}
	public static final class ServerCode_isExecuted_withImplicitName extends DataVerifier {
		@Override
		public void verify() throws Exception {
			Assert.assertEquals("ServerCodeImplicit", this.em.find(TestingEntity.class, 1L).getName());
		}
	}
}
