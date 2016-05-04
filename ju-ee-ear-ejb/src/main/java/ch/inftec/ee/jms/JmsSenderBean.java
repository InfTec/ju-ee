package ch.inftec.ee.jms;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;

/**
 * Bean to put messages on a JMS queue.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
@Stateless
public class JmsSenderBean {
	@Inject
	private Logger logger;
	
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/ju")
	private Queue queue;

	public void send(String text) {
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		
		try {
			connection = this.connectionFactory.createConnection();
			
			// Transacted means that all messages sent using this session will be
			// sent in a transaction that must be committed.
			// For details, see: http://www2.sys-con.com/itsg/virtualcd/Java/archives/0604/chappell/index.html
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			TextMessage m = session.createTextMessage(text);

			producer = session.createProducer(queue);
			
			logger.info("Sending text message {}", text);
			producer.send(m);

			logger.info("Sent text message {}", text);
		} catch (JMSException ex) {
			logger.error("Couldn't send JMS message", ex);
		} finally {
			try {
				if (producer != null) producer.close();
				if (session != null) session.close();
				if (connection != null) connection.close();
			} catch (JMSException ex) {
				logger.error("Could not close connection and/or session: " + ex);
			}
		}
	}
}
