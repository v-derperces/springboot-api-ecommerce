package fr.afpa.pompey.APIBoulangerie.controller;

import fr.afpa.pompey.APIBoulangerie.model.Produit;
import fr.afpa.pompey.APIBoulangerie.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @PostMapping("/produit")
    public Produit createProduit(@RequestBody Produit produit) {
        return produitService.saveProduit(produit);
    }

    @GetMapping("/produits")
    public Iterable<Produit> getProduits() {
        return produitService.getProduits();
    }

    @GetMapping("/produit/{id}")
    public Produit getProduit(@PathVariable("id") int id) {
        Optional<Produit> produit = produitService.getProduit(id);
        if(produit.isPresent()){
            return produit.get();
        }
        return null;
    }

    @PutMapping("/produit/{id}")
    public Produit updateProduit(@RequestBody Produit produit, @PathVariable("id") int id) {
        Optional<Produit> p = produitService.getProduit(id); // Récupère le produit dans la BDD.
        if(p.isPresent()){ // Vérifie si le produit existe.
            Produit prod = p.get();
            String libProduit = produit.getLibProduit();
            if(libProduit != null && !libProduit.isEmpty()){
                prod.setLibProduit(libProduit);
            }
            produitService.saveProduit(produit);
            return prod;
        }
        return null;
    }

    @DeleteMapping("/produit/{id}")
    public void deleteProduit(@PathVariable("id") int id) {
        produitService.deleteProduit(id);
    }
}
