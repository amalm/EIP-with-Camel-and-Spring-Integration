package eip.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.entities.BacklogItem;

public class BacklogServiceMemory implements BacklogService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BacklogServiceMemory.class);
	private final List<BacklogItem> list;
	
	public BacklogServiceMemory() {
		list = new ArrayList<BacklogItem>();
	}

	@Override
	public void saveBacklogItems(Backlog backlog) {
		for (BacklogItem backlogItem : backlog.getItems()) {
			LOGGER.info("Saving Backlog item:{}", backlogItem.toString());
			list.add(backlogItem);
		}
	}

	@Override
	public List<BacklogItem> getBacklogItems() {
		return Collections.unmodifiableList(list);
	}

}
