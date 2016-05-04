package ch.inftec.ju.ee.test;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.inftec.ju.testing.db.DataVerifier;
import ch.inftec.ju.testing.db.DataVerify;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataVerifyIT extends ContainerTest {
	@Test
	@DataVerify()
	public void a1_dataVerify_findsDefaultVerifier() {
	}
	public static class A1_dataVerify_findsDefaultVerifier extends DataVerifier {
		private static boolean executed = false;
		
		@Override
		public void verify() {
			executed = true;
		}
	}
	
	@Test
	public void a2_defaultVerifier_wasExecuted() {
		Assert.assertTrue(A1_dataVerify_findsDefaultVerifier.executed);
	}
	
	@Test
	@DataVerify(B_customVerifier.class)
	public void b1_dataVerify_findsCustomVerifier() {
	}
	public static class B_customVerifier extends DataVerifier {
		private static boolean executed = false;
		
		@Override
		public void verify() {
			executed = true;
		}
	}
	
	@Test
	public void b2_defaultVerifier_wasExecuted() {
		Assert.assertTrue(B_customVerifier.executed);
	}
	
	@Test
	@DataVerify()
	public void _01_c1_dataVerify_findsDefaultVerifier_whenUsing_numberPrefixing() {
	}
	public static class C1_dataVerify_findsDefaultVerifier_whenUsing_numberPrefixing extends DataVerifier {
		private static boolean executed = false;
		
		@Override
		public void verify() {
			executed = true;
		}
	}
	
	@Test
	public void _01_c1_dataVerify_wasExecuted() {
		Assert.assertTrue(C1_dataVerify_findsDefaultVerifier_whenUsing_numberPrefixing.executed);
	}
}
