package eis.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stock {
	private static Logger LOGGER = LoggerFactory.getLogger(Stock.class);
	
	private Map<String, Integer> itemsOnStock = new HashMap<String, Integer>();
	
	public Integer getItems(String itemNumber)
	{
		if (!itemsOnStock.containsKey(itemNumber))
			return 0;
		else
			return itemsOnStock.get(itemNumber);
	}
	public void addItem(Item item)
	{
		LOGGER.info("Add {}", item.toString());
		if (!itemsOnStock.containsKey(item.getNumber()))
			itemsOnStock.put(item.getNumber(), 1);
		else
			itemsOnStock.put(item.getNumber(), itemsOnStock.get(item.getNumber())+1);
	}

	public void addItems(Item item, Integer items)
	{
		if (!itemsOnStock.containsKey(item.getNumber()))
			itemsOnStock.put(item.getNumber(), items);
		else
			itemsOnStock.put(item.getNumber(), itemsOnStock.get(item.getNumber())+items);
	}
	
	public void removeItem(Item item)
	{
		if (itemsOnStock.containsKey(item.getNumber()))
			itemsOnStock.put(item.getNumber(), itemsOnStock.get(item.getNumber())-1);
		
	}
}
