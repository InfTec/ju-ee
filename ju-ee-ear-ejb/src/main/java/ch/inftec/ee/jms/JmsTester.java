package ch.inftec.ee.jms;

import java.util.ArrayList;
import java.util.List;

public class JmsTester {
	private static final List<String> messages = new ArrayList<>();
	
	/**
	 * Waits until there are no messages left on the queue and then clears the internal debug list.
	 */
	public static void waitAndClear() {
		// TODO: PR-285 Commented out as we've removed utils for now...
//		// Use JMX to make sure message count is 0
//		ConcurrencyUtils.waitFor(new Predicate<Void>() {
//			@Override
//			public boolean apply(Void input) {
//				Long messageCount = JBossUtils.queryJmx().jms().queueInfo("juQueue").getMessageCount();
//				return messageCount == 0;
//			}
//		});
//		
//		messages.clear();
	}
	
	public static synchronized void messageReceived(String message) {
		messages.add(message);
	}
	
	public static synchronized List<String> getMessages() {
		return new ArrayList<>(messages);
	}
	
	public static List<String> waitAndGet(final int size) {
		// TODO: PR-285 Commented out as we've removed utils for now...
//		ConcurrencyUtils.waitFor(new Predicate<Void> () {
//			@Override
//			public boolean apply(Void input) {
//				return getMessages().size() == size;
//			}
//		});
//		
//		return getMessages();
		return null;
	}
}
