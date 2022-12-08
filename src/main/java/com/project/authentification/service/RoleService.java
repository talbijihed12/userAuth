package com.project.authentification.service;


import com.communication.plateforme.services.IRoleService;
import com.communication.plateforme.utils.exceptions.BadRequestException;
import com.project.authentification.model.Role;
import com.project.authentification.model.enums.RoleEnum;
import com.project.authentification.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleService implements IRoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role addRole(RoleEnum roleEnum) {
        if (roleExist(roleEnum))
            throw new BadRequestException("Role exist");
        return roleRepository.save(new Role(roleEnum));
    }

    @Override
    public boolean roleExist(RoleEnum roleEnum) {
        return roleRepository.findByName(roleEnum).isPresent();
    }

    @Override
    public List<Role> listRole() {
        return this.roleRepository.findAll();
    }

    @Override
    public List<Role> listRoleByRoleEnumsList(List<RoleEnum> roleEnums) {
        return roleEnums.stream().map(this::findRoleByRoleEnum).collect(Collectors.toList());
    }

    @Override
    public Role findRoleByRoleEnum(RoleEnum roleEnum) {
        Optional<Role> roleOptional = this.roleRepository.findByName(roleEnum);
        return roleOptional.orElseGet(() -> this.roleRepository.save(new Role(roleEnum)));
    }
}
