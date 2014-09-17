package eip.spring.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import eip.common.services.StockService;

/**
 * Demo implementation looping until expected items are found on the db.
 * Not really a good test implementation, thus not named according to name pattern 
 * automatically detected by surefire.
 * @author Anders Malmborg
 *
 */
@ContextConfiguration(locations={
		"classpath:META-INF/springintegration.spring.xml", 
		 "classpath:META-INF/jpa.spring.xml",
		"classpath:META-INF/services.db.spring.xml"})
public class DeliveryNoteCsvImportDemo extends AbstractTransactionalTestNGSpringContextTests {
	
	private static final String STOCK_NR = "1935182366";
	@Autowired
	private StockService stockService;
 	
	@Test
	public void check() throws Exception
	{
		for (int i = 0; i < 3; i++)
		{
			Thread.sleep(1000);
			Integer itemsOnStock = stockService.getItemsOnStock(STOCK_NR);
			if (itemsOnStock > 0)
			{
				assertEquals(itemsOnStock, Integer.valueOf(2));
				assertNotNull(stockService.getStockItem("1935182439"));
				return;
			}
		}
		fail("Expected a stock item "+STOCK_NR);
	}

}
