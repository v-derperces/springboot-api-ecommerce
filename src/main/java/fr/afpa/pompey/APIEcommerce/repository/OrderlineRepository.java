package fr.afpa.pompey.APIEcommerce.repository;

import fr.afpa.pompey.APIEcommerce.model.Orderline;
import fr.afpa.pompey.APIEcommerce.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderlineRepository extends CrudRepository<Orderline,Integer> {
    boolean existsByProduct(Product product);
}
