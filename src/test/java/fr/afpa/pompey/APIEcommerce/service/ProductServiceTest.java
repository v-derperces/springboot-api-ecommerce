package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.*;
import fr.afpa.pompey.APIEcommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderlineRepository orderlineRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product product;
    private User user;
    private Category category;
    private Order order;
    private Orderline orderLine;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setName("Expensive");
        categoryRepository.save(category);

        product = new Product();
        product.setName("Laptop");
        product.setCategories(new ArrayList<>(List.of(category)));
        productRepository.save(product);
    }

    @Test
    void createProductDuplicateName() {
        Product duplicate = new Product();
        duplicate.setName(product.getName());
        duplicate.setCategories(new ArrayList<>(List.of(category)));

        CustomHttpException ex = assertThrows(CustomHttpException.class,
                () -> productService.saveProduct(duplicate));

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void deleteProduct_success() throws CustomHttpException {

        productService.deleteProduct(product.getProductId());

        Optional<Product> deleted = productRepository.findById(product.getProductId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteProduct_throwsException_ProductLinkedToOrder() {
        user = new User();
        user.setFirstName("Ron");
        user.setLastName("Weasley");
        user.setEmail("ron@hogwarts.com");
        user.setPassword("Babbity*Rabbity");
        userRepository.save(user);

        order = new Order();
        order.setUser(user);
        orderRepository.save(order);

        orderLine = new Orderline();
        orderLine.setOrder(order);
        orderLine.setProduct(product);
        orderLine.setQuantity(5);
        orderlineRepository.save(orderLine);

        CustomHttpException ex = assertThrows(CustomHttpException.class,
                () -> productService.deleteProduct(product.getProductId()));

        assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode());
        assertTrue(ex.getMessage().contains("linked to an order"));

        Optional<Product> stillExists = productRepository.findById(product.getProductId());
        assertTrue(stillExists.isPresent());
    }
}
