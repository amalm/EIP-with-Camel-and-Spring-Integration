package eip.common.services;

import java.util.ArrayList;
import java.util.List;

import eip.common.entities.BacklogItem;
import eip.common.entities.Customer;
import eip.common.entities.Item;
import eip.common.entities.Order;
import eip.common.entities.OrderItem;
import eip.common.entities.OrderItemStatus;
import eip.common.entities.StockItem;
import eip.common.repositories.CustomerRepository;

public class OrderServiceDb implements OrderService {
	private final StockService stockService;
	private final CustomerRepository customerRepository;

	public OrderServiceDb(StockService stockService, CustomerRepository customerRepository) {
		this.stockService = stockService;
		this.customerRepository = customerRepository;
	}

	@Override
	public Backlog handleOrder(Order order) {

		List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
		Customer customer = customerRepository.findByName(order.getCustomerName());
		if (customer == null)
		{
			customer = new Customer(order.getCustomerName());
		}
		customer.getOrders().add(order);
		for (OrderItem orderItem : order.getOrderItems()) {
			orderItem.setStatus(OrderItemStatus.BACKLOG);
			StockItem stockItem = stockService.getStockItem(orderItem.getItem().getNumber());
			if (stockItem != null)
			{
				if (stockItem.getQuantity() > 0) {
					orderItem.setStatus(OrderItemStatus.CHECKED_OUT);
					stockService.checkoutStockItem(stockItem);
					continue;
				}
			}
			backlogItems.add(new BacklogItem(new Item(orderItem.getItem())));
		}
		customerRepository.save(customer);
		return new Backlog(backlogItems);
	}
}
