package eip.spring.integration;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import eip.common.entities.Item;
import eip.common.entities.ItemType;
import eip.common.entities.Order;
import eip.common.entities.OrderItem;

public class OrderFlatFileItemReaderDelegate implements ItemReader<Order> {
	private final FlatFileItemReader<FieldSet> delegate;
	private Order nextOrder = null;

	public OrderFlatFileItemReaderDelegate(
			FlatFileItemReader<FieldSet> delegate) {
		this.delegate = delegate;
	}

	public void setResource(Resource resource) {
		delegate.setResource(resource);
	}

	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		delegate.open(executionContext);
	}
	

	public void close() throws ItemStreamException {
		delegate.close();
	}

	@Override
	public Order read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		FieldSet fieldSet = delegate.read();
		Order order = null;
		while (fieldSet != null) {
			if (nextOrder != null)
				order = nextOrder;
			String prefix = fieldSet.readString(0);
			if (prefix.equals("ORDER"))
			{
				if (order != null)
				{
					nextOrder = new Order(fieldSet.readString("customerName"), fieldSet.readString("orderNumber")); 
					return order;
				}
				else
					order = new Order(fieldSet.readString("customerName"), fieldSet.readString("orderNumber"));
			}
			else if (prefix.equals("ITEM"))
			{
				Assert.notNull(order, "order must not be null");
				ItemType itemType;
				String type = fieldSet.readString("itemType");
				if (type.equals(ItemType.DRIVE.toString()))
					itemType = ItemType.DRIVE;
				else if (type.equals(ItemType.FRAME.toString()))
					itemType = ItemType.FRAME;
				else if (type.equals(ItemType.WHEEL.toString()))
					itemType = ItemType.WHEEL;
				else if (type.equals(ItemType.OTHER.toString()))
					itemType = ItemType.OTHER;
				else
					throw new BindException(this, "No match for itemType '"+type+"'");

				Item item = new Item(itemType, fieldSet.readString("name"), fieldSet.readString("number"));
				order.getOrderItems().add(new OrderItem(item));
			}
			else
				throw new ParseException("No record matching "+prefix);
			fieldSet = delegate.read();
		}
		return order;
	}
}
