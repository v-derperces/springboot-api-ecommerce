package fr.afpa.pompey.APIEcommerce.repository;

import fr.afpa.pompey.APIEcommerce.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
