package eip.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.Assert;

import eip.common.services.MailNotification;
import eip.common.services.Notification;
import eip.common.services.SmsNotification;

public class NotificationEnricher implements Processor {

	@Override
	public void process(final Exchange exchange) throws Exception {
		Notification notification = exchange.getIn()
				.getBody(Notification.class);
		Assert.notNull(notification, "Not a Notification message");
		if (notification.getCustomer().equals("customerWithSms"))
			exchange.getIn().setBody(
					new SmsNotification(notification.getCustomer(),
							notification.getMessage(), "smsNumber"));
		else
			exchange.getIn().setBody(
					new MailNotification(notification.getCustomer(),
							"mailAddress", "mailSubject", notification
									.getMessage()));
	}
}
