package ch.inftec.ju.ee.client;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(RemoteCalculator.class)
public class CalculatorBean implements RemoteCalculator {

	@Override
	public int add(int a, int b) {
		return a + b;
	}

}
