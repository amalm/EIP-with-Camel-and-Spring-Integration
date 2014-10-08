package eip.common.services;

public class MailNotification extends Notification {
	private static final long serialVersionUID = 1L;
	private final String address;
	private final String subject;
	public MailNotification(String customer, String address, String subject, String message) {
		super(customer, message);
		this.address = address;
		this.subject = subject;
	}
	public String getAddress() {
		return address;
	}
	public String getSubject() {
		return subject;
	}

}
