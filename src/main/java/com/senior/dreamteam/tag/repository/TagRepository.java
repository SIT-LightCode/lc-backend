package com.senior.dreamteam.tag.repository;


import com.senior.dreamteam.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findAll();

    Optional<Tag> findTagById(int id);

}
