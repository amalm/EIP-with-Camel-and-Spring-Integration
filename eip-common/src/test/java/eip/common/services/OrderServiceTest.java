package eip.common.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import eip.common.entities.Customer;
import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.entities.Order;
import eip.common.entities.OrderItem;
import eip.common.entities.StockItem;
import eip.common.repositories.CustomerRepository;
import eip.common.services.Backlog;
import eip.common.services.OrderServiceDb;
import eip.common.services.StockServiceDb;

public class OrderServiceTest {
	private OrderService target;
	@Mock
	private StockServiceDb stockService;
	@Mock
	private CustomerRepository customerRepository;
	@BeforeMethod
	public void beforeMethod()
	{
		MockitoAnnotations.initMocks(this);
		target = new OrderServiceDb(stockService, customerRepository);
	}
	
	@Test(dataProvider="createOrder")
	public void handleOrder(Order order, Item item1, Item item2, StockItem stockItem1, StockItem stockItem2)
	{
		when(stockService.getStockItem(item1.getNumber())).thenReturn(stockItem1);
		when(stockService.getStockItem(item2.getNumber())).thenReturn(stockItem2);
		Customer customer = new Customer(order.getCustomerName());
		when(customerRepository.findByName(order.getCustomerName())).thenReturn(customer);
		Backlog backlog = target.handleOrder(order);
		verify(stockService).checkoutStockItem(stockItem1);
		verify(stockService, times(0)).checkoutStockItem(stockItem2);
		assertEquals(backlog.getItems().size(), 1);
	}
	
	@Test(dataProvider="createOrder")
	public void handleOrderUnknownItem(Order order, Item item1, Item item2, StockItem stockItem1, StockItem stockItem2)
	{
		Backlog backlog = target.handleOrder(order);
		verify(stockService, times(0)).checkoutStockItem(stockItem1);
		verify(stockService, times(0)).checkoutStockItem(stockItem2);
		assertEquals(backlog.getItems().size(), 2);
	}

	@DataProvider
	private Object[][] createOrder()
	{
    	Item item1 = new Item(ItemType.DRIVE, "Shimano LX", "1234");
    	Item item2 = new Item(ItemType.FRAME, "Black 60 cm", "5678");
    	Set<OrderItem> orderItems = new HashSet<OrderItem>(Arrays.asList(new OrderItem(item1),new OrderItem(item2)));
		Order order = new Order("customer", "order1", orderItems);
    	StockItem stockItem1 = new StockItem(item1, Integer.valueOf(1));
    	StockItem stockItem2 = new StockItem(item2, Integer.valueOf(0));
	    return new Object[][] {
	        {
	            order,
	            item1,
	            item2,
	            stockItem1,
	            stockItem2
	        }
	    };
	}
}
