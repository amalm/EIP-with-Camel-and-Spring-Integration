package eis.domain.services;

import java.util.ArrayList;
import java.util.List;

import eis.domain.entities.Customer;
import eis.domain.entities.Item;
import eis.domain.entities.Order;
import eis.domain.entities.OrderItem;
import eis.domain.entities.OrderItemStatus;
import eis.domain.entities.StockItem;
import eis.domain.repositories.CustomerRepository;

public class OrderService {
	private final StockService stockService;
	private final CustomerRepository customerRepository;

	public OrderService(StockService stockService, CustomerRepository customerRepository) {
		this.stockService = stockService;
		this.customerRepository = customerRepository;
	}

	public Backlog handleOrder(Order order) {

		List<Item> backlogItems = new ArrayList<Item>();
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
			backlogItems.add(new Item(orderItem.getItem()));
		}
		customerRepository.save(customer);
		return new Backlog(backlogItems);
	}
}
