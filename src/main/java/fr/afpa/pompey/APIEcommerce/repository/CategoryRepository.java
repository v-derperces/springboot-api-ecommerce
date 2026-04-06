package fr.afpa.pompey.APIEcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.afpa.pompey.APIEcommerce.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
