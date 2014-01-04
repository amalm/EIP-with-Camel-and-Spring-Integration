package eip.common.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Customer {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name="CUSTOMER_ID")
	private Set<Order> orders;
	
	Customer()
	{
		this(null);
	}
	public Customer(String name) {
		this.name = name;
		orders = new HashSet<Order>();
	}
	public String getName() {
		return name;
	}
	public Set<Order> getOrders() {
		return orders;
	}
}
