package eip.common.services;

import java.util.List;

import eip.common.entities.BacklogItem;

public class Backlog {
	private List<BacklogItem> items;

	public Backlog(List<BacklogItem> items) {
		this.items = items;
	}

	public List<BacklogItem> getItems() {
		return items;
	}
	
}
