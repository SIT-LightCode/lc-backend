package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();

    Optional<User> findUserByEmail(String email);
}
