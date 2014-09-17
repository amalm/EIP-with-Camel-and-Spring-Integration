package eip.common.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class BacklogItem {
	@GeneratedValue
	@Id
	private Long id;
	@Embedded
	private Item item;

	
	BacklogItem()
	{
		this(null);
	}
	
	public BacklogItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	@Override
	public String toString() {
		return item.toString();
	}
	
}
