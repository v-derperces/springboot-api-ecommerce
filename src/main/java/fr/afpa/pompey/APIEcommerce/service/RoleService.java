package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.mapper.RoleMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDTO).toList();
    }

    public RoleResponse getRole(Long id) throws CustomHttpException {
        Role role = roleRepository.findById(id)
        .orElseThrow(() -> new CustomHttpException("Role not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));
        return roleMapper.toDTO(role);
    }

    public RoleResponse createRole(RoleRequest request) throws CustomHttpException {
        try{
            Role role = roleMapper.toEntity(request);
            return roleMapper.toDTO(roleRepository.save(role));
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("Role '" + request.getName() + "' already exists.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public RoleResponse updateRole(Long id, RoleRequest request) throws CustomHttpException {
        Role existingRole = roleRepository.findById(id)
        .orElseThrow(() -> new CustomHttpException("Role not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));
        try{
            existingRole.setName(request.getName());
            return roleMapper.toDTO(roleRepository.save(existingRole));
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("Role '" + request.getName() + "' already exists.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteRole(Long id) throws CustomHttpException {
        try{
            roleRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new CustomHttpException("Role cannot be deleted. It is associated to a user",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

}
