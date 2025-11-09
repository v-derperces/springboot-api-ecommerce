package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Product;
import fr.afpa.pompey.APIEcommerce.repository.OrderlineRepository;
import fr.afpa.pompey.APIEcommerce.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderlineRepository  orderlineRepository;

    public ProductService(ProductRepository productRepository, OrderlineRepository orderlineRepository) {
        this.productRepository = productRepository;
        this.orderlineRepository = orderlineRepository;
    }

    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProduct(int id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) throws CustomHttpException {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Product '" + product.getName() + "' already exists.",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteProduct(int id) throws CustomHttpException {

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product p = product.get();
            if (orderlineRepository.existsByProduct(p)) {
                throw new CustomHttpException("The product cannot be deleted. It is linked to an order",
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase());
            }
            productRepository.deleteById(id);
        }
    }
}
