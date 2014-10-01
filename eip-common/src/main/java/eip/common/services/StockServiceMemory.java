package eip.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eip.common.entities.StockItem;

public class StockServiceMemory implements StockService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceMemory.class);
	
	@Override
	public Integer getItemsOnStock(String itemNumber) {
		return Integer.valueOf(2);
	}

	@Override
	public StockItem getStockItem(String itemNumber) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkoutStockItem(StockItem stockItem) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addStockItem(StockItem stockItem) {
		LOGGER.info("Adding Stock Item {} to stock", stockItem.toString());
	}

}
