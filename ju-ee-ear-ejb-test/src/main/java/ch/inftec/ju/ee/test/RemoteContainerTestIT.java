package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.testing.db.DataSet;

public class RemoteContainerTestIT extends RemoteContainerTesterTest {
	@Override
	protected Class<?> getTesterClass() {
		return RemoteContainerTestITTester.class;
	}
	
	@Test
	public void containerTest_initializes_serviceLocator() {
		Assert.assertNotNull(this.serviceLocator);
	}
	
	@Test
	@DataSet("RemoteContainerTestIT_testingEntity1.xml")
	public void canRunMethodInEjbContext() {
		Assert.assertEquals("RemoteContainerTestIT1", this.remoteGetTestingEntityName(-1L));
	}
	
	private String remoteGetTestingEntityName(Long id) {
		return this.callRemoteMethod("getTestingEntityName"
				, new Class<?>[] {Long.class}
				, new Object[] {id});
	}
}