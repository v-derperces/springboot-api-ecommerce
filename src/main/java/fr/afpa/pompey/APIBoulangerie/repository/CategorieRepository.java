package fr.afpa.pompey.APIBoulangerie.repository;

import fr.afpa.pompey.APIBoulangerie.model.Categorie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends CrudRepository<Categorie, Integer> {
}
