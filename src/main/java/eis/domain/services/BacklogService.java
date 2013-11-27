package eis.domain.services;

import java.io.File;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;

import eis.domain.entities.Item;

public class BacklogService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BacklogService.class);
	private final FlatFileItemWriter<Item> writer;
	private final File backlogDir;
	private static File lastFile;

	public BacklogService(FlatFileItemWriter<Item> writer, File backlogDir) {
		this.writer = writer;
		this.backlogDir = backlogDir;
	}
	
	public void orderBacklogItems(Backlog backlog)
	{
		LOGGER.info("backlogs:{}", backlog.getItems().toString());
		File file = new File(backlogDir, Long.valueOf(Calendar.getInstance().getTimeInMillis()).toString()+".txt");
		writer.setResource(new FileSystemResource(file));
		writer.open(new ExecutionContext());
		try {
			writer.write(backlog.getItems());
		} catch (Exception e) {
			throw new RuntimeException("Could not write backlog items", e);
		} finally {
			writer.close();
		}
		lastFile = file;
	}

	/**
	 * Only for unit tests.
	 * @return last written file
	 */
	public static File getLastFile() {
		return lastFile;
	}
	
	
}
