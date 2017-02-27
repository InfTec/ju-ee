package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ch.inftec.ju.testing.db.DataSet;
import ch.inftec.ju.testing.db.DataSetExport;
import ch.inftec.ju.util.DataHolder;

@Ignore("TODO: Fix for REST: Problem with remote method invocation with Long parameter")
public class TransactionsIT extends RemoteContainerTesterTest {
	@Override
	protected Class<?> getTesterClass() {
		return TransactionITTester.class;
	}
	
	/**
	 * Test case using multiple (2) transactions. The test case looks as follows:
	 * {@code
	 * T1
	 * 	T2
	 * Enter
	 * 	Enter
	 * 	E1 -> null
	 * Create E1
	 * 	E1 -> obj
	 * End (commit)
	 * 	E1 -> obj
	 * 	Create E2
	 * 	End (commit)
	 * 
	 * Check:
	 * 	E1 -> obj
	 * 	E2 -> obj
	 * }
	 */
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml")
	public void testMultipleTransactions() {
		final DataHolder<Long> id1 = new DataHolder<>();
		final DataHolder<Long> id2 = new DataHolder<>();
		
		this.remoteClearSignals();
		
		new Thread(new Runnable() {
			public void run() {
				id1.setValue(TransactionsIT.this.<Long>callRemoteMethod("transaction1"));
			};
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				id2.setValue(TransactionsIT.this.<Long>callRemoteMethod("transaction2"));
			};
		}).start();
		
		Assert.assertEquals("T1", this.remoteGetTestingEntityName(id1.waitForValue(10000)));
		this.remoteSendSignal(5);
		
		Assert.assertEquals("T2", this.remoteGetTestingEntityName(id2.waitForValue(10000)));
	}
	
	/**
	 * Test case using multiple (2) transactions. The test case looks as follows:
	 * {@code
	 * T1
	 * 	T2
	 * Enter
	 * 	Enter
	 * 	E1 -> null
	 * Create E1
	 * 	E1 -> obj
	 * End (rollback)
	 * 	E1 -> null
	 * 	Create E2
	 * 	End
	 * 
	 * Check:
	 * 	E1 -> null
	 * 	E2 -> obj
	 * }
	 */
	@Test
	@DataSet("ju-testing/data/default-noData.xml")
	@DataSetExport(tablesDataSet="ju-testing/data/default-noData.xml")
	public void testMultipleTransactions_withRollback() {
		final DataHolder<Exception> ex1 = new DataHolder<>();
		final DataHolder<Long> id2 = new DataHolder<>();
		
		this.remoteClearSignals();
		
		new Thread(new Runnable() {
			public void run() {
				try {
					TransactionsIT.this.<Long>callRemoteMethod("transaction1_withRollback");
				} catch (Exception ex) {
					ex1.setValue(ex);
				}
			};
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				id2.setValue(TransactionsIT.this.<Long>callRemoteMethod("transaction2_withRollback"));
			};
		}).start();
		
		Assert.assertTrue(ex1.waitForValue(10000).getMessage().endsWith("Rolling back: 1"));
		this.remoteSendSignal(5);
		
		Assert.assertEquals("T2", this.remoteGetTestingEntityName(id2.waitForValue(10000)));
	}
	
	private String remoteGetTestingEntityName(Long id) {
		return this.callRemoteMethod("getTestingEntityName", new Class<?>[] {Long.class}, new Object[] {id});
	}
}
