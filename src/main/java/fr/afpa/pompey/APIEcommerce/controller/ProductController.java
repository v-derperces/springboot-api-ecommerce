package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Category;
import fr.afpa.pompey.APIEcommerce.model.Product;
import fr.afpa.pompey.APIEcommerce.service.CategoryService;
import fr.afpa.pompey.APIEcommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    private ProductService productService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping("/product")
    public Product createProduct(@Valid @RequestBody Product product) throws CustomHttpException {

        List<Category> categories = new ArrayList<>();
        if (product.getCategories() != null) {
            for (Category c : product.getCategories()) {
                Category cat = categoryService.getCategory(c.getCategoryId())
                        .orElseThrow(() -> new CustomHttpException("Category not found: " + c.getCategoryId(),
                                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase() ));
                categories.add(cat);
            }
        }

        product.setCategories(categories);
        return productService.saveProduct(product);
    }

    @GetMapping("/products")
    public Iterable<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProduct(id);
        if(product.isPresent()){
            return product.get();
        }
        return null;
    }

    @PutMapping("/product/{id}")
    public Product updateProduct(@Valid @RequestBody Product product, @PathVariable("id") Long id) throws CustomHttpException {
        Optional<Product> p = productService.getProduct(id);
        if(p.isPresent()){ // Checks if the product already exists.
            List<Category> categories = new ArrayList<>();
            if (product.getCategories() != null) {
                for (Category c : product.getCategories()) {
                    Category cat = categoryService.getCategory(c.getCategoryId())
                            .orElseThrow(() -> new CustomHttpException("Category not found: " + c.getCategoryId(),
                                    HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase() ));
                    categories.add(cat);
                }
            }

            Product prod = p.get();
            prod.setUnitPrice(product.getUnitPrice());
            prod.setCategories(product.getCategories());
            prod.setName(product.getName());
            prod.setCategories(product.getCategories());

            productService.saveProduct(prod);
            return prod;
        }
        return null;
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable("id") Long id) throws CustomHttpException {
        productService.deleteProduct(id);
    }
}
