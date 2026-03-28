package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.mapper.RoleMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
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

    public RoleResponse getRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(
            ()-> new NotFoundException("Cannot get role: No role found with id: " + id)
        );

        return roleMapper.toDTO(role);
    }

    public RoleResponse createRole(RoleRequest request){
        try{
            Role role = roleMapper.toEntity(request);
            return roleMapper.toDTO(roleRepository.save(role));
        }catch (DataIntegrityViolationException e){
            throw new ConflictException("Cannot create role: a role with name '" + request.getName() + "' already exists");
        }
    }

    public RoleResponse updateRole(Long id, RoleRequest request){
        Role existingRole = roleRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Cannot update role: No role found with id: " + id)
        );
        try{
            existingRole.setName(request.getName());
            return roleMapper.toDTO(roleRepository.save(existingRole));
        }catch (DataIntegrityViolationException e){
            throw new ConflictException("Cannot update role: a role with name '" + request.getName() + "' already exists");
        }
    }

    public void deleteRole(Long id) {
        try{
            roleRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new ConflictException("Role with id " + id + "cannot be delete because it is associated with a user.");
        }
    }

    public Role getRoleByName(String name) {
        Role role = roleRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("Cannot get role: No role found with name: " + name));

        return role;
    }

}
