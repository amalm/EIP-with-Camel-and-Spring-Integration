package eip.common.services;

import java.io.Serializable;

public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String customer;
	private final String message;

	public Notification(String customer, String message) {
		this.customer = customer;
		this.message = message;
	}

	
	public String getCustomer() {
		return customer;
	}


	public String getMessage() {
		return message;
	}
	
}
