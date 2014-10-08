package eip.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailServiceLog implements MailService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MailServiceLog.class);

	@Override
	public void send(Notification notification) {
		MailNotification mailNotification = (MailNotification)notification;
		LOGGER.info("Sending mail to '{}' with subject '{}'", mailNotification.getAddress(), mailNotification.getSubject());
	}
}
