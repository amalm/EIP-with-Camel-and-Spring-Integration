package eis.domain.si;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import eis.domain.entities.Customer;
import eis.domain.repositories.CustomerRepository;
import eis.domain.services.BacklogService;

@ContextConfiguration(locations={"classpath:META-INF/order.spring.xml", "classpath:META-INF/jpa.spring.xml"})
public class OrderCsvImportTest extends AbstractTransactionalTestNGSpringContextTests {
	
	private static final String CUSTOMER_NAME = "Bike support";
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BacklogService backlogService;
	
	@Test
	public void check() throws InterruptedException, FileNotFoundException
	{
		for (int i = 0; i < 3; i++)
		{
			Thread.sleep(200);
			if (BacklogService.getLastFile() != null)
			{
				Customer customer = customerRepository.findByName(CUSTOMER_NAME);
				assertEquals(customer.getOrders().size(), 1);
				assertEquals(customer.getOrders().iterator().next().getOrderItems().size(), 2);
				File backlogFile = BacklogService.getLastFile();
				Scanner scanner = new Scanner(backlogFile);
				// last order contains one item
				assertEquals(scanner.nextLine(), "Spoke 28 inches;098876");
				try {
					scanner.nextLine();
					fail("Should not come here, one lines expected");
				}
				catch (NoSuchElementException e)
				{
					
				}
				scanner.close();
				return;
			}
		}
		fail("Expected a customer "+CUSTOMER_NAME);
	}

}
