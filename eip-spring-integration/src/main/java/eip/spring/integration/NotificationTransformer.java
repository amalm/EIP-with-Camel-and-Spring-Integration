package eip.spring.integration;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import eip.common.services.MailNotification;
import eip.common.services.Notification;
import eip.common.services.SmsNotification;

public class NotificationTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		Notification notification = (Notification) message.getPayload();
		Notification outNotification;
		if (notification.getCustomer().equals("customerWithSms"))
			outNotification = new SmsNotification(notification.getCustomer(),
					notification.getMessage(), "smsNumber");
		else
			outNotification = new MailNotification(notification.getCustomer(),
					"mailAddress", "mailSubject", notification.getMessage());
		return MessageBuilder.withPayload(outNotification).build();
	}

}
