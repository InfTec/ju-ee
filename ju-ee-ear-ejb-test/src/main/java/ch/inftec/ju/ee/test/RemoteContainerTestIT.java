package ch.inftec.ju.ee.test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.RemoteContainerTestITTester.ComplexParam;
import ch.inftec.ju.ee.test.RemoteContainerTestITTester.ComplexResult;
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
	
	@Test
	public void givenComplexParameters_runMethodInEjbContext_succeeds() {
		ComplexParam complexParam = new ComplexParam();
		
		complexParam.longList.add(0L);
		complexParam.longList.add(1L);
		
		complexParam.map.put("key0", "val {/} 0");
		complexParam.map.put("key1", "val {/} 1");
		
		Object resObject = callRemoteMethod("getComplexResult"
				, new Class<?>[] { Long.class, String.class, ComplexParam.class }
				, new Object[] { 1L, "foo {/} bar", complexParam });
		
		assertThat(resObject, is(instanceOf(ComplexResult.class)));
		
		ComplexResult res = (ComplexResult) resObject;
		
		assertThat(res.longVal, is(1L));
		assertThat(res.stringVal, is("foo {/} bar"));
		
		assertThat(res.complexParam.longList.size(), is(2));
		assertThat(res.complexParam.longList.get(0), is(0L));
		assertThat(res.complexParam.longList.get(1), is(1L));
		
		assertThat(res.complexParam.map.size(), is(2));
		assertThat(res.complexParam.map.get("key0"), is("val {/} 0"));
		assertThat(res.complexParam.map.get("key1"), is("val {/} 1"));
	}
}