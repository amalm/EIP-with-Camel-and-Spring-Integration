package eip.common.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class OrderItem {
	@Id
	@GeneratedValue
	private Long id;
	@Embedded
	private Item item;
	private OrderItemStatus status;

	OrderItem()
	{
		this(null);
	}
	public OrderItem(Item item) {
		this.item = item;
	}

	public Long getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public OrderItemStatus getStatus() {
		return status;
	}

	public void setStatus(OrderItemStatus status) {
		this.status = status;
	}
	

}
