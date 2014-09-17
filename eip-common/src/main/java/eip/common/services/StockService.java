package eip.common.services;

import eip.common.entities.StockItem;

public interface StockService {

	Integer getItemsOnStock(String itemNumber);

	StockItem getStockItem(String itemNumber);

	void checkoutStockItem(StockItem stockItem);

	void addStockItem(StockItem stockItem);

}