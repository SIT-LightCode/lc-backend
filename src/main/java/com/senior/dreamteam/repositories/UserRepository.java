package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(int id);


    Authorities findByName(Roles name);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.authorities")
    List<User> findAllWithAuthorities();
}
