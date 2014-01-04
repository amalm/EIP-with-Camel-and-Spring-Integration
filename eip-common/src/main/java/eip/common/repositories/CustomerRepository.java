package eip.common.repositories;

import org.springframework.data.repository.CrudRepository;

import eip.common.entities.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	Customer findByName(String name);
}
