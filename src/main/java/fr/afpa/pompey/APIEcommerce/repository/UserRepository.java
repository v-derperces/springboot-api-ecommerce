package fr.afpa.pompey.APIEcommerce.repository;

import fr.afpa.pompey.APIEcommerce.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    Optional<User> getUserByEmail(String email);

    boolean existsUserByEmail(String email);
}
