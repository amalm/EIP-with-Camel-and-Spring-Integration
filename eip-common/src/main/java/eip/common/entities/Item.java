package eip.common.entities;

import javax.persistence.Embeddable;

@Embeddable
public class Item {
	private ItemType itemType;
	private String name;
	private String number;
	private Double price;
	
	Item()
	{
		this(null, null, null, null);
	}

	public Item(ItemType itemType, String name, String number, Double price) {
		this.itemType = itemType;
		this.name = name;
		this.number = number;
		this.price = price;
	}
	public Item(ItemType itemType, String name, String number) {
		this(itemType, name, number, null);
	}

	public Item(Item item) {
		this(item.getItemType(), item.getName(), item.getNumber());
	}
	
	public ItemType getItemType() {
		return itemType;
	}

	public String getName() {
		return name;
	}
	public String getNumber() {
		return number;
	}
	public Double getPrice() {
		return price;
	}
	@Override
	public String toString() {
		return "itemType:"+itemType + ", name:" + name+",number:"+number;
	}
	
}
