package eis.domain.services;

import java.util.List;

import eis.domain.entities.Item;

public class Backlog {
	private List<Item> items;

	public Backlog(List<Item> items) {
		this.items = items;
	}

	public List<Item> getItems() {
		return items;
	}
	
}
