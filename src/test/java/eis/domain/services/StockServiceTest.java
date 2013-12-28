package eis.domain.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import eis.domain.entities.Item;
import eis.domain.entities.StockItem;
import eis.domain.entities.ItemType;
import eis.domain.repositories.StockItemRepository;

public class StockServiceTest {
	private StockService target;
	private String itemNr;
	private StockItem item;
	@Mock
	private StockItemRepository repository;
	
	@BeforeMethod
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		target = new StockService(repository);
		itemNr = UUID.randomUUID().toString();
		item = new StockItem(new Item(ItemType.DRIVE, "test", itemNr, Double.valueOf(0)), Integer.valueOf(1));
	}
	
	@Test
	public void addOneItem()
	{
		target.addStockItem(item);
		verify(repository).save(item);
	}

	@Test
	public void getStockItem()
	{
		when(repository.findByItemNumber(item.getItem().getNumber())).thenReturn(item);
		assertEquals(target.getStockItem(item.getItem().getNumber()), item);
	}
	
	@Test
	public void getItemsOnStock()
	{
		when(repository.findByItemNumber(item.getItem().getNumber())).thenReturn(item);
		assertEquals(target.getItemsOnStock(item.getItem().getNumber()), Integer.valueOf(1));
		assertEquals(target.getItemsOnStock("2"), Integer.valueOf(0));
	}
	
	@Test
	public void checkoutStockItem()
	{
		target.checkoutStockItem(item);
		verify(repository).save(Mockito.any(StockItem.class));
	}
}
