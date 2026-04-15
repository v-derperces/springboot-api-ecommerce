package com.vderperces.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vderperces.ecommerce.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
