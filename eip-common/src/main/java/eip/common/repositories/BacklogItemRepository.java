package eip.common.repositories;

import org.springframework.data.repository.CrudRepository;

import eip.common.entities.BacklogItem;

public interface BacklogItemRepository extends CrudRepository<BacklogItem, Long> {
	BacklogItem findByItemNumber(String itemNumber);
}
