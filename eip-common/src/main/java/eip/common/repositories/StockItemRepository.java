package eip.common.repositories;

import org.springframework.data.repository.CrudRepository;

import eip.common.entities.StockItem;

public interface StockItemRepository extends CrudRepository<StockItem, Long> {
	StockItem findByItemNumber(String itemNumber);
}
