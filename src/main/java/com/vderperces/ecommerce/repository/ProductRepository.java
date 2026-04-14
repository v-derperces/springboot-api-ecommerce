package com.vderperces.ecommerce.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vderperces.ecommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveFalse(Pageable pageable);

    Optional<Product> findByProductIdAndActiveTrue(Long id);
}
