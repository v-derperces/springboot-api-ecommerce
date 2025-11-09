package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Iterable<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRole(int id) {
        return roleRepository.findById(id);
    }

    public Role saveRole(Role role) throws CustomHttpException {
        try{
            return roleRepository.save(role);
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("Role '" + role.getName() + "' already exists.",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteRole(int id) throws CustomHttpException {
        try{
            roleRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("Role cannot be deleted. It is associated to a user",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

}
