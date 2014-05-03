package eip.camel;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import eip.common.entities.BacklogItem;
import eip.common.entities.Customer;
import eip.common.repositories.BacklogItemRepository;
import eip.common.repositories.CustomerRepository;

@ContextConfiguration(locations={"classpath:META-INF/order.camel.spring.xml",
								 "classpath:META-INF/jpa.spring.xml",
								 "classpath:META-INF/services.spring.xml"})
public class OrderCsvImportDemo extends AbstractTransactionalTestNGSpringContextTests {
	
	private static final String CUSTOMER_NAME = "Bike support";
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BacklogItemRepository backlogItemRepository;
	
	@Test
	public void check() throws InterruptedException, FileNotFoundException
	{
		for (int i = 0; i < 3; i++)
		{
			Thread.sleep(300);
			BacklogItem backlogItem = backlogItemRepository.findByItemNumber("098876");
			if (backlogItem != null)
			{
				Customer customer = customerRepository.findByName(CUSTOMER_NAME);
				assertEquals(customer.getOrders().size(), 1);
				assertEquals(customer.getOrders().iterator().next().getOrderItems().size(), 2);
				return;
			}
		}
		fail("Expected a customer "+CUSTOMER_NAME);
	}

}
