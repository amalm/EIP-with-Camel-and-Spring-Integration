package eip.common.services;

import java.util.List;

import eip.common.entities.BacklogItem;

public interface BacklogService {

	void saveBacklogItems(Backlog backlog);

	List<BacklogItem> getBacklogItems();

}