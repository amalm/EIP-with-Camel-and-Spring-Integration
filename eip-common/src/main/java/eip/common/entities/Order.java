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
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="CUST_ORDER")
public class Order {
	@Id
	@GeneratedValue
	private Long id;
	private String number;
	@Transient
	private String customerName;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name="ORDER_ID")
	private Set<OrderItem> orderItems;

	Order()
	{
		this(null, null, null);
	}
	public Order(String customerName, String number, Set<OrderItem> orderItems) {
		this.customerName = customerName;
		this.number = number;
		this.orderItems = orderItems;
	}

	public Order(String customerName, String number) {
		this(customerName, number, new HashSet<OrderItem>());
	}

	public Long getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}
	
}
