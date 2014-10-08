package eip.camel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Demonstrates the ActiveMQ component as well as Enricher and Dynamic Router.
 * Sends a Notfication, that is enriched to either a SMS or mail notification according to
 * the customer settings.
 * The enriched messages is dynamically routed to either a SMS or mail service.
 * @author Anders Malmborg
 *
 */
@ContextConfiguration(locations={"classpath:META-INF/activemq.spring.xml", "classpath:smsmail.camel.spring.xml"})
public class ActiveMqEnricherRoutingTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private SmsService smsServiceMock;
	@Autowired
	private MailService mailServiceMock;

	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Test
	public void sendSms() throws InterruptedException
	{
		CountDownLatch latch = new CountDownLatch(1);
		Mockito.doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(smsServiceMock)
				.send(Mockito.any(SmsNotification.class));
		producerTemplate.sendBody("direct:notification", new Notification("customerWithSms", "smsMessage"));
		Assert.assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		Mockito.verify(smsServiceMock, Mockito.times(1)).send(Mockito.any(SmsNotification.class));
	}

	@Test
	public void sendMail() throws InterruptedException
	{
		CountDownLatch latch = new CountDownLatch(1);
		Mockito.doAnswer(new CountDownLatchAnswer().countsDownLatch(latch)).when(mailServiceMock)
				.send(Mockito.any(MailNotification.class));
		producerTemplate.sendBody("direct:notification", new Notification("customerWithMail", "mailMessage"));
		Assert.assertTrue(latch.await(3, TimeUnit.SECONDS), "latch interupted");
		Mockito.verify(mailServiceMock, Mockito.times(1)).send(Mockito.any(MailNotification.class));
	}
}
