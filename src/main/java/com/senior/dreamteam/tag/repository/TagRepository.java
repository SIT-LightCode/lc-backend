package com.senior.dreamteam.tag.repository;


import com.senior.dreamteam.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findAll();

}
