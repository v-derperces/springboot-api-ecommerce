package fr.afpa.pompey.APIBoulangerie.service;

import fr.afpa.pompey.APIBoulangerie.model.Categorie;
import fr.afpa.pompey.APIBoulangerie.repository.CategorieRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    public Iterable<Categorie> getCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> getCategorie(int id) {
        return categorieRepository.findById(id);
    }

    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public void deleteCategorie(int id) {
        categorieRepository.deleteById(id);
    }
}
