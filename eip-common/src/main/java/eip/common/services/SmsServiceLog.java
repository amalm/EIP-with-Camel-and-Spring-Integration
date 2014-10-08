package eip.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsServiceLog implements SmsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SmsServiceLog.class);

	@Override
	public void send(final Notification notification) {
		LOGGER.info("Sending sms to '{}'", ((SmsNotification)notification).getNumber());
	}

}
