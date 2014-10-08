package eip.common.services;


public class SmsNotification extends Notification {
	private static final long serialVersionUID = 1L;
	private final String number;
	
	public SmsNotification(String customer, final String message, final String number) {
		super(customer, message);
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

}
