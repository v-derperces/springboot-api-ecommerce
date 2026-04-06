package fr.afpa.pompey.APIEcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.afpa.pompey.APIEcommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
