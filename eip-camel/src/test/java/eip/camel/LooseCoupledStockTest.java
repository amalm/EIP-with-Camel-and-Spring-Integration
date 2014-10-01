package eip.camel;

import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import eip.common.services.StockService;

@ContextConfiguration(locations="classpath:loosecoupledstock.camel.spring.xml")
public class LooseCoupledStockTest extends AbstractTestNGSpringContextTests {
	
	@Autowired
	@Qualifier("stockService")
	private StockService stockService;
	
	@Test
	public void getStockItem()
	{
		assertEquals(stockService.getItemsOnStock("itemNumber"), Integer.valueOf(2));
	}

}
