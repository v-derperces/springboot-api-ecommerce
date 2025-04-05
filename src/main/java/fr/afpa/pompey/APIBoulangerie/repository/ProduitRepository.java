package fr.afpa.pompey.APIBoulangerie.repository;

import fr.afpa.pompey.APIBoulangerie.model.Produit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduitRepository extends CrudRepository<Produit, Integer> {
}
