package eip.spring.integration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import eip.common.services.MailNotification;
import eip.common.services.MailService;
import eip.common.services.Notification;
import eip.common.services.SmsNotification;
import eip.common.services.SmsService;
import eip.common.testutil.CountDownLatchAnswer;

@ContextConfiguration(locations={"classpath:META-INF/activemq.spring.xml", "classpath:smsmail.spring.xml"})
public class ActiveMqEnricherRoutingTest extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("transformerChannel")
	private MessageChannel messageChannel;
	@Autowired
	private SmsService smsServiceMock;
	@Autowired
	private MailService mailServiceMock;

	
	@Test
	public void sendSms() throws InterruptedException
	{
		CountDownLatch latch = new CountDownLatch(1);
		Mockito.doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(smsServiceMock)
				.send(Mockito.any(SmsNotification.class));
		messageChannel.send(MessageBuilder.withPayload(new Notification("customerWithSms", "smsMessage")).build());
		Assert.assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		Mockito.verify(smsServiceMock, Mockito.times(1)).send(Mockito.any(SmsNotification.class));
	}

	@Test
	public void sendMail() throws InterruptedException
	{
		CountDownLatch latch = new CountDownLatch(1);
		Mockito.doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(mailServiceMock)
				.send(Mockito.any(MailNotification.class));
		messageChannel.send(MessageBuilder.withPayload(new Notification("customerWithMail", "mailMessage")).build());
		Assert.assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		Mockito.verify(mailServiceMock, Mockito.times(1)).send(Mockito.any(MailNotification.class));
	}
}
