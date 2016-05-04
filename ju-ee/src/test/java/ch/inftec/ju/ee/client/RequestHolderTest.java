package ch.inftec.ju.ee.client;

import org.junit.Assert;
import org.junit.Test;

import ch.inftec.ju.ee.test.sim.RequestHolder;

public class RequestHolderTest {
	@Test
	public void canPut_andPoll_requests() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";
		String req1b = "req1b";
		Integer req2 = 1;

		rh.putRequest(String.class, req1a);
		rh.putRequest(Integer.class, req2);
		rh.putRequest(String.class, req1b);

		Assert.assertEquals(req1a, rh.pollRequest(String.class));
		Assert.assertEquals(req1b, rh.pollRequest(String.class));
		Assert.assertEquals(req2, rh.pollRequest(Integer.class));
	}

	@Test
	public void canPoll_lastRequest() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";
		String req1b = "req1b";

		rh.putRequest(String.class, req1a);
		rh.putRequest(String.class, req1b);

		Assert.assertEquals(req1b, rh.pollLastRequest(String.class));
		Assert.assertNull(rh.pollRequest(String.class));
	}

	@Test
	public void canPeek_Request() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";
		String req1b = "req1b";

		rh.putRequest(String.class, req1a);
		rh.putRequest(String.class, req1b);

		Assert.assertEquals(req1a, rh.peekRequest(String.class));
		Assert.assertEquals(2, rh.getRequestCount(String.class));
	}

	@Test
	public void canPeek_LastRequest() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";
		String req1b = "req1b";

		rh.putRequest(String.class, req1a);
		rh.putRequest(String.class, req1b);

		Assert.assertEquals(req1b, rh.peekLastRequest(String.class));
		Assert.assertEquals(2, rh.getRequestCount(String.class));
	}

	@Test
	public void canPeek_fromEndWithOffset() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";
		String req1b = "req1b";

		rh.putRequest(String.class, req1a);
		rh.putRequest(String.class, req1b);

		Assert.assertEquals(req1b, rh.peekFromEndWithOffset(String.class, 0));
		Assert.assertEquals(req1a, rh.peekFromEndWithOffset(String.class, 1));
		Assert.assertEquals(2, rh.getRequestCount(String.class));
	}

	@Test
	public void peekFromEndWithOffset_returnsNull_forOutOfBoundsOffset() {
		RequestHolder rh = new RequestHolder();

		String req1a = "req1a";

		rh.putRequest(String.class, req1a);

		Assert.assertNull(rh.peekFromEndWithOffset(String.class, 1));
		Assert.assertEquals(1, rh.getRequestCount(String.class));
	}
}
