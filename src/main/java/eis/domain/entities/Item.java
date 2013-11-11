package eis.domain.entities;

import java.util.Calendar;
import java.util.Date;

public class Item {
	private String name;
	private String number;
	private Double price;
	private Date updated;

	
	public Item(String name, String number, Double price) {
		super();
		this.name = name;
		this.number = number;
		this.price = price;
		setUpdated(Calendar.getInstance().getTime());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	@Override
	public String toString() {
		return name+","+number;
	}
	
}
