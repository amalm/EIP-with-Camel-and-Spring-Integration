package eis.domain.repositories;

import org.springframework.data.repository.CrudRepository;

import eis.domain.entities.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	Customer findByName(String name);
}
