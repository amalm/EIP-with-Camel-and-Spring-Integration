package eip.camel;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.services.MailNotification;
import eip.common.services.Notification;
import eip.common.services.SmsNotification;

public class NotificationRouter {

	private static final String ACTIVEMQ_MAIL = "activemq:mail";
	private static final String ACTIVEMQ_SMS = "activemq:sms";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NotificationRouter.class);

	public String slip(final Notification notification,
			@Properties Map<String, Object> properties) {
		// End routing by returning null, otherwise endless loop
		// The current endpoint is in the properties.
		// First run - where routing should be done - it will be null
		if (properties.get(Exchange.SLIP_ENDPOINT) != null) {
			return null;
		}
		if (notification instanceof SmsNotification) {
			LOGGER.info("Route to {}", ACTIVEMQ_SMS);
			return ACTIVEMQ_SMS;
		} else if (notification instanceof MailNotification) {
			LOGGER.info("Route to {}", ACTIVEMQ_MAIL);
			return ACTIVEMQ_MAIL;
		}
		return null;
	}

}
