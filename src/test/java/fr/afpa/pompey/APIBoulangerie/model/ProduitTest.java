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
class ProduitTest {
    
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie aux pépites de chocolat", "Chausson aux pommes, chausson à la crème"})
    void setLibProduitValid(String lib) {
        Produit c = new Produit();
        c.setLibProduit(lib);
        Set<ConstraintViolation<Produit>> violations = validator.validate(c);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Texte très très très long, et faisant plus de 50 caractères"})
    void setLibProduitInvalid(String lib) {
        Produit c = new Produit();
        c.setLibProduit(lib);
        Set<ConstraintViolation<Produit>> violations = validator.validate(c);
        assertFalse(violations.isEmpty());
    }
}