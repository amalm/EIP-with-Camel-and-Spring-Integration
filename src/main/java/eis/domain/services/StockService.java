package eis.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import eis.domain.entities.StockItem;
import eis.domain.repositories.StockItemRepository;

public class StockService {
	private static Logger LOGGER = LoggerFactory.getLogger(StockService.class);
	
	private final StockItemRepository stockItemRepository;
	StockService()
	{
		this(null);
	}
	public StockService(final StockItemRepository stockItemRepository) {
		this.stockItemRepository = stockItemRepository;
	}

	public Integer getItemsOnStock(String itemNumber)
	{
		StockItem stockItem =  getStockItem(itemNumber);
		if (stockItem == null)
			return 0;
		else
			return stockItem.getQuantity();
	}

	public StockItem getStockItem(String itemNumber)
	{
		return stockItemRepository.findByItemNumber(itemNumber);
	}
	
	public void checkoutStockItem(StockItem stockItem)
	{
		stockItem.setQuantity(stockItem.getQuantity()-1);
		stockItemRepository.save(stockItem);
	}

	@Transactional
	public void addStockItem(StockItem stockItem)
	{
		StockItem item = stockItemRepository.findByItemNumber(stockItem.getItem().getNumber());
		if (item == null)
		{
			stockItemRepository.save(stockItem);
		}
		else
		{
			item.setQuantity(item.getQuantity()+stockItem.getQuantity());
		}
		LOGGER.info("Imported {}", stockItem.toString());
	}
	
	@Override
	public String toString() {
		Iterable<StockItem> stockItems = stockItemRepository.findAll();
		StringBuilder bld = new StringBuilder("Stock:");
		String sep = "\n";
		for (StockItem stockItem : stockItems)
		{
			bld.append(sep).append(stockItem.toString());
		}
		return bld.toString();
	}
	
}
