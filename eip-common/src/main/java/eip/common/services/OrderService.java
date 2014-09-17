package eip.common.services;

import eip.common.entities.Order;

public interface OrderService {

	Backlog handleOrder(Order order);

}