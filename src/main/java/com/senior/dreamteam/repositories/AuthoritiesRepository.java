package com.senior.dreamteam.repositories;

import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Example;
import com.senior.dreamteam.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {

    Authorities findByName(Roles name);

    List<Authorities> findByUserId(int userId);
}
