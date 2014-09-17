package eip.camel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import eip.common.services.StockService;

@ContextConfiguration(locations={
		"classpath:META-INF/camel.spring.xml",
		 "classpath:META-INF/jpa.spring.xml",
		 "classpath:META-INF/services.db.spring.xml"})
public class DeliveryNoteCsvImportDemo extends AbstractTransactionalTestNGSpringContextTests {
	
	private static final String STOCK_NR = "1935182366";
	@Autowired
	private StockService stock;
	
	
	@Test
	public void check() throws InterruptedException
	{
		for (int i = 0; i < 3; i++)
		{
			Thread.sleep(1000);
			Integer itemsOnStock = stock.getItemsOnStock(STOCK_NR);
			if (itemsOnStock > 0)
			{
				Assert.assertEquals(itemsOnStock, Integer.valueOf(2));
				return;
			}
		}
		Assert.fail("Expected a stock item "+STOCK_NR);
	}
}