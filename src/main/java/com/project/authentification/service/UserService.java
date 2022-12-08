package com.project.authentification.service;


import com.communication.plateforme.services.IRoleService;

import com.project.authentification.model.Role;
import com.project.authentification.model.User;
import com.project.authentification.payload.request.UpdateUserRequest;
import com.project.authentification.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    IRoleService roleService;

    @Override
    public List<User> listUser() {
        return userRepo.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepo.existsById(id))
            userRepo.deleteById(id);
        else {
            throw new NotFoundException("User with id: " + id + " not found");
        }
    }

    @Override
    public void resetPassword(String email, String password) {

    }

    @Override
    public void updateUser(UpdateUserRequest updateUserRequest) {
        User user = this.findUserByUsername(updateUserRequest.getUsername());
        List<Role> roles = this.roleService.listRoleByRoleEnumsList(updateUserRequest.getRoles());
        user.setRoles(new HashSet<>(roles));
        this.userRepo.save(user);

    }

    @Override
    public User findUserByUsername(String username) {
        return userRepo.findByNom(username).orElseThrow(() -> new NotFoundException("user not found with name -" + username));

    }
}
