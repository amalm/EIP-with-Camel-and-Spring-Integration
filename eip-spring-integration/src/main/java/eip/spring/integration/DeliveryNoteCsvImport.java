package eip.spring.integration;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;

import eip.common.entities.StockItem;

public class DeliveryNoteCsvImport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryNoteCsvImport.class);
	private FlatFileItemReader<StockItem> reader;
	private ItemWriter<StockItem> writer;
	
	public DeliveryNoteCsvImport(FlatFileItemReader<StockItem> reader,
			ItemWriter<StockItem> writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public void importCsv(Message<File> message) throws Exception {
		LOGGER.info("Importing file {}", message.getPayload().getAbsolutePath());
		Resource resource = new FileSystemResource(message.getPayload());
		reader.setResource(resource);
		reader.open(new ExecutionContext());
		StockItem item;
		try {
			item = reader.read();
			while(item != null)
			{
				LOGGER.info("Read:{}", item.toString());
				writer.write(Arrays.asList(item));
				item = reader.read();
			}
		}
		finally {
			reader.close();
		}
	}

}
