package ch.inftec.ee.jms;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;

/**
 * Bean to receive messages from a JMS queue.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination",
				propertyValue = "jms/queue/juQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",
				propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "maxSession",
				propertyValue = "1")
})
public class JmsReceiverBean implements MessageListener {
	@Inject
	private Logger logger;
	
	@Resource
	private MessageDrivenContext mdc;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage tm = (TextMessage) message;
				logger.info("Received object {}", tm.getText());
				
				JmsTester.messageReceived(tm.getText());
			} else {
				logger.error("Couldn't process message " + message);
			}
		} catch (Exception ex) {
			mdc.setRollbackOnly();
			logger.error("Couldn't process message " + message, ex);
		}
	}
}
