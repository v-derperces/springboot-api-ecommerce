package fr.afpa.pompey.APIBoulangerie.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategorieTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Gateau", "Cake", "cookie", "Bûche de Noël"})
    void setLibCategorieValid(String lib) {
        Categorie c = new Categorie();
        c.setLibCategorie(lib);
        Set<ConstraintViolation<Categorie>> violations = validator.validate(c);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Texte très long, et faisant plus de 30 caractères"})
    void setLibCategorieInvalid(String lib) {
        Categorie c = new Categorie();
        c.setLibCategorie(lib);
        Set<ConstraintViolation<Categorie>> violations = validator.validate(c);
        assertFalse(violations.isEmpty());
    }
}