package eis.domain.si;

import java.io.File;
import java.util.Arrays;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.Message;

import eis.domain.entities.StockItem;

public class DeliveryNoteCsvImport {
	private FlatFileItemReader<StockItem> reader;
	private ItemWriter<StockItem> writer;
	
	public DeliveryNoteCsvImport(FlatFileItemReader<StockItem> reader,
			ItemWriter<StockItem> writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public void importCsv(Message<?> message) throws Exception {
		Resource resource = new FileSystemResource((File)message.getPayload());
		reader.setResource(resource);
		reader.open(new ExecutionContext());
		StockItem item;
		try {
			item = reader.read();
			while(item != null)
			{
				writer.write(Arrays.asList(item));
				item = reader.read();
			}
		}
		finally {
			reader.close();
		}
	}

}
