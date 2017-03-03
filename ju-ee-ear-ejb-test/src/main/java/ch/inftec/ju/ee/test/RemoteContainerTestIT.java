package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
	
	@Test
	public void canRunParameterlessMethodInEjbContext() {
		assertThat(remoteGetConstValue(), is("Some Const Value"));
	}
	
	private String remoteGetConstValue() {
		return this.callRemoteMethod("getConstValue"
				, null
				, null);
	}
	
	@Test
	public void canRunVoidMethodInEjbContext() {
		assertThat(remoteGetVoid(), is(nullValue()));
	}
	
	private String remoteGetVoid() {
		return this.callRemoteMethod("getVoid"
				, null
				, null);
	}
}