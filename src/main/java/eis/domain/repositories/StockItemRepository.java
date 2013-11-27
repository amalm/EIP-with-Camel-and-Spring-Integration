package eis.domain.repositories;

import org.springframework.data.repository.CrudRepository;

import eis.domain.entities.StockItem;

public interface StockItemRepository extends CrudRepository<StockItem, Long> {
	StockItem findByItemNumber(String itemNumber);
}
