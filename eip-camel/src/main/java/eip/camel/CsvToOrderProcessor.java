package eip.camel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.Assert;

import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.entities.Order;
import eip.common.entities.OrderItem;

public class CsvToOrderProcessor implements Processor {


	@Override
	public void process(Exchange exchange) throws Exception {
		String csvString = exchange.getIn().getBody(String.class);
		//[[, Bike support, 1], [ITEM, FRAME, Road bike frame 60 cm, 1935182366], [ITEM, DRIVE, Shimano HG LX, 1935182439]]
		List<String> recs = Arrays.asList(csvString.split("\\],"));
		Assert.isTrue(recs.size() > 1);
		String orderString  = recs.get(0).replace("[", "");
		orderString = orderString.replace("]", "");
		List<String> orderStrings = Arrays.asList(orderString.split(","));
		Assert.isTrue(orderStrings.size() == 3);
		String customerName = orderStrings.get(1).trim();
		String orderNumber = orderStrings.get(2).trim();
	
		Set<OrderItem> orderItems = new HashSet<OrderItem>();
		for (int i = 1; i < recs.size(); i++) {
			orderString  = recs.get(i).replace("[", "");
			orderString = orderString.replace("]", "");
			orderStrings = Arrays.asList(orderString.split(","));
			Assert.isTrue(orderStrings.size() == 4);
			ItemType itemType = ItemType.OTHER;
			String type = orderStrings.get(1).trim();
			if (type.equals(ItemType.FRAME.toString()))
				itemType = ItemType.FRAME;
			else if (type.equals(ItemType.DRIVE.toString()))
				itemType = ItemType.DRIVE;
			else if (type.equals(ItemType.WHEEL.toString()))
				itemType = ItemType.WHEEL;
			OrderItem orderItem = new OrderItem(new Item(itemType, orderStrings.get(2).trim(), orderStrings.get(3).trim()));
			orderItems.add(orderItem);
		}
		Order order = new Order(customerName, orderNumber, orderItems);
		exchange.getIn().setBody(order);
	}
}
