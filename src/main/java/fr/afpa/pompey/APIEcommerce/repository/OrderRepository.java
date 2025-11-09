package fr.afpa.pompey.APIEcommerce.repository;

import fr.afpa.pompey.APIEcommerce.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order,Integer> {
}
