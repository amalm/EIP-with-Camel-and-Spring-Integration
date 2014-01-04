package eip.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.repositories.BacklogItemRepository;

public class BacklogService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BacklogService.class);
	private final BacklogItemRepository repository;

	public BacklogService(BacklogItemRepository repository) {
		this.repository = repository;
	}

	public void orderBacklogItems(Backlog backlog) {
		LOGGER.info("backlogs:{}", backlog.getItems().toString());
		repository.save(backlog.getItems());
	}
}
