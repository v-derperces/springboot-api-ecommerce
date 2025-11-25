package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Category;
import fr.afpa.pompey.APIEcommerce.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categorieRepository;

    public CategoryService(CategoryRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    public Iterable<Category> getCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Category> getCategory(int id) {
        return categorieRepository.findById(id);
    }

    public Category saveCategory(Category categorie) throws CustomHttpException {
        try {
            return categorieRepository.save(categorie);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Category '" + categorie.getCategoryId()
                    + "' already exists in database.",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteCategory(int id) throws CustomHttpException {
        try{
            categorieRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("A category linked to a product cannot be deleted.",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }
}
