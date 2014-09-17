package eip.common.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.entities.BacklogItem;
import eip.common.entities.Item;
import eip.common.entities.Order;
import eip.common.entities.OrderItem;

public class OrderServiceMemory implements OrderService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceMemory.class);
	@Override
	public Backlog handleOrder(Order order) {
		List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
		// Demo implemetation: instead of "real" db logic, consider each second order item 
		// to be on the stock and each other to be added to the backlog.
		LOGGER.info("Processing order for customer {}", order.getCustomerName());
		int i = 0;
		for (OrderItem orderItem : order.getOrderItems()) {
			if (i % 2 == 0) {
				LOGGER.info("Order item {} can be fullfilled, is on stock", orderItem.getItem().getName());
			}
			else {
				LOGGER.info("Order item {} can not be fullfilled, not enough on stock", orderItem.getItem().getName());
				backlogItems.add(new BacklogItem(new Item(orderItem.getItem())));
			}
		}
		return new Backlog(backlogItems);
	}

}
