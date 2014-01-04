package eip.common.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class StockItem {
	@GeneratedValue
	@Id
	private Long id;
	@Embedded
	private Item item;
	private Integer quantity;

	
	StockItem()
	{
		this(null, null);
	}
	
	public StockItem(Item item, Integer quantity) {
		this.item = item;
		this.quantity = quantity;
	}
	public Item getItem() {
		return item;
	}
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return item.toString()+", quantity:"+quantity;
	}
	
}
