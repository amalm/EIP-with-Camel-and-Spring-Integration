package eis.domain.entities;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StockTest {
	private Stock target;
	private String itemNr;
	private Item item;
	
	@BeforeMethod
	public void setUp()
	{
		target = new Stock();
		itemNr = UUID.randomUUID().toString();
		item = new Item("test", itemNr, Double.valueOf(0));
	}
	
	@Test
	public void addOneItem()
	{
		target.addItem(item);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(1));
	}

	@Test
	public void addMoreItem()
	{
		target.addItem(item);
		target.addItem(item);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(2));
	}
	
	@Test
	public void addItems()
	{
		target.addItems(item, 6);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(6));
		target.addItems(item, 4);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(10));
	}

	@Test
	public void removeItem()
	{
		target.addItem(item);
		target.removeItem(item);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(0));
	}
	@Test
	public void removeItemNotOnStock()
	{
		target.removeItem(item);
		Assert.assertEquals(target.getItems(itemNr), Integer.valueOf(0));
	}

}
