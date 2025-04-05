package fr.afpa.pompey.APIBoulangerie.controller;

import fr.afpa.pompey.APIBoulangerie.model.Categorie;
import fr.afpa.pompey.APIBoulangerie.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CategorieController {
    @Autowired
    private CategorieService categorieService;

    @PostMapping("/categorie")
    public Categorie createCategorie(@RequestBody Categorie categorie) {
        return categorieService.saveCategorie(categorie);
    }

    @GetMapping("/categories")
    public Iterable<Categorie> getCategories() {
        return categorieService.getCategories();
    }

    @GetMapping("/categorie/{id}")
    public Categorie getCategorie(@PathVariable("id") int id) {
        Optional<Categorie> categorie = categorieService.getCategorie(id);
        if(categorie.isPresent()){
            return categorie.get();
        }
        return null;
    }

    @PutMapping("/categorie/{id}")
    public Categorie updateCategorie(@RequestBody Categorie categorie, @PathVariable("id") int id) {
        Optional<Categorie> p = categorieService.getCategorie(id); // Récupère la catégorie dans la BDD.
        if(p.isPresent()){ // Vérifie si la catégorie existe.
            Categorie cat = p.get();
            String libCategorie = categorie.getLibCategorie();
            if(libCategorie != null && !libCategorie.isEmpty()){
                cat.setLibCategorie(libCategorie);
            }
            categorieService.saveCategorie(cat); // Met à jour la catégorie.
            return cat;
        }
        return null;
    }

    @DeleteMapping("/categorie/{id}")
    public void deleteCategorie(@PathVariable("id") int id) {
        categorieService.deleteCategorie(id);
    }
}
