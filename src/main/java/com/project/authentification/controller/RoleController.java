package com.project.authentification.controller;

import com.communication.plateforme.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list-role")
    public ResponseEntity listUsers() {

        return new ResponseEntity<>(roleService.listRole(), HttpStatus.OK);
    }
}
