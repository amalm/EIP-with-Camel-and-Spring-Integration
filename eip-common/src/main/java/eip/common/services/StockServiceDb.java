package eip.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import eip.common.entities.StockItem;
import eip.common.repositories.StockItemRepository;

public class StockServiceDb implements StockService {
	private static Logger LOGGER = LoggerFactory.getLogger(StockServiceDb.class);
	
	private final StockItemRepository stockItemRepository;
	/*
	 * For Spring.
	 */
	StockServiceDb()
	{
		this(null);
	}
	public StockServiceDb(final StockItemRepository stockItemRepository) {
		this.stockItemRepository = stockItemRepository;
	}

	@Override
	public Integer getItemsOnStock(String itemNumber)
	{
		StockItem stockItem =  getStockItem(itemNumber);
		if (stockItem == null)
			return 0;
		else
			return stockItem.getQuantity();
	}

	@Override
	public StockItem getStockItem(String itemNumber)
	{
		return stockItemRepository.findByItemNumber(itemNumber);
	}
	
	@Override
	public void checkoutStockItem(StockItem stockItem)
	{
		stockItem.setQuantity(stockItem.getQuantity()-1);
		stockItemRepository.save(stockItem);
	}

	@Override
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
