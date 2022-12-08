package com.project.authentification.repositories;

import com.project.authentification.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByNom(String nom);


    Boolean existsByNom(String username);

    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
