package fr.afpa.pompey.APIBoulangerie.service;

import fr.afpa.pompey.APIBoulangerie.model.Produit;
import fr.afpa.pompey.APIBoulangerie.repository.ProduitRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    public Iterable<Produit> getProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduit(int id) {
        return produitRepository.findById(id);
    }

    public Produit saveProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public void deleteProduit(int id) {
        produitRepository.deleteById(id);
    }
}
