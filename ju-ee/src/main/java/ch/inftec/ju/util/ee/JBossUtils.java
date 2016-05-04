package ch.inftec.ju.util.ee;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import ch.inftec.ju.util.jmx.MBeanUtils;

/**
 * JBoss specific utility functions.
 * @author martin.meyer@inftec.ch
 *
 */
public class JBossUtils {
	public static MBeanServer getMBeanServer() {
		MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
		return server;
	}
	
	/**
	 * Returns a helper object to query JBoss info through JMX.
	 * <p>
	 * Uses fluent API style, e.g. queryJmx().jms().queueInfo("queueName").getMessageCount()
	 * @return JmxInfoQuery instance.
	 */
	public static JmxInfoQuery queryJmx() {
		return new JmxInfoQuery();
	}
	
	public static final class JmxInfoQuery {
		public JmsInfoQuery jms() {
			return new JmsInfoQuery();
		}
		
		public static final class JmsInfoQuery {
			public QueueInfoQuery queueInfo(String queueName) {
				return new QueueInfoQuery(queueName);
			}
			
			public static final class QueueInfoQuery {
				private final String queueName;
				
				private QueueInfoQuery(String queueName) {
					this.queueName = queueName;
				}
				
				public Long getMessageCount() {
					Long messageCount = MBeanUtils.queryPlatformMBeanServer(
								String.format("jboss.as:subsystem=messaging,hornetq-server=default,jms-queue=%s", this.queueName))
							.getAttribute("messageCount").get(Long.class);
					return messageCount;
				}
			}
		}
	}
}
