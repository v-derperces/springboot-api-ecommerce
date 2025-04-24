package fr.afpa.pompey.APIBoulangerie.service;

import fr.afpa.pompey.APIBoulangerie.exceptionhandler.DuplicationException;
import fr.afpa.pompey.APIBoulangerie.model.Categorie;
import fr.afpa.pompey.APIBoulangerie.repository.CategorieRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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

    public Categorie saveCategorie(Categorie categorie) throws DuplicationException {
        try {
            return categorieRepository.save(categorie);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicationException("La catégorie '" + categorie.getLibCategorie()
                    + "' existe déjà dans la base de données.");
        }
    }

    public void deleteCategorie(int id) {
        categorieRepository.deleteById(id);
    }
}
