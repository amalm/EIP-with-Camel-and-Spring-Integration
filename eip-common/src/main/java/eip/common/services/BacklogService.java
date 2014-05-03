package eip.common.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.entities.BacklogItem;
import eip.common.repositories.BacklogItemRepository;

public class BacklogService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BacklogService.class);
	private final BacklogItemRepository repository;

	public BacklogService(BacklogItemRepository repository) {
		this.repository = repository;
	}

	public void saveBacklogItems(Backlog backlog) {
		LOGGER.info("backlogs:{}", backlog.getItems().toString());
		repository.save(backlog.getItems());
	}
	
	public List<BacklogItem> getBacklogItems()
	{
		List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
		Iterator<BacklogItem> iterator = repository.findAll().iterator();
		while (iterator.hasNext())
		{
			backlogItems.add(iterator.next());			
		}
		return backlogItems;
	}
}
