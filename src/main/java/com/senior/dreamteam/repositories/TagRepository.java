package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT 1", nativeQuery = true)
    void executeNativeQuery();
    List<Tag> findAll();
    Optional<Tag> findTagById(int id);
}
