package eip.spring.integration;

import java.io.File;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import eip.common.entities.Order;

public class OrderCsvImport {
	private OrderFlatFileItemReaderDelegate reader;
	private MessagingTemplate messagingTemplate;
	
	public OrderCsvImport(OrderFlatFileItemReaderDelegate reader, MessageChannel channel) {
		this.reader = reader;
		this.messagingTemplate = new MessagingTemplate(channel);
	}

	public void importCsv(Message<?> message) throws Exception {
		Resource resource = new FileSystemResource((File)message.getPayload());
		reader.setResource(resource);
		reader.open(new ExecutionContext());
		Order order;
		try {
			order = reader.read();
			while(order != null)
			{
				messagingTemplate.send(MessageBuilder.withPayload(order).build());
				order = reader.read();
			}
		}
		finally {
			reader.close();
		}
	}

}
