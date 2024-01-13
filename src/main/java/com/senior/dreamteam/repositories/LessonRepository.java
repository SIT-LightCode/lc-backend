package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findAll();
    List<Lesson> findAllByTag_Id(int tagId);
    Optional<Lesson> findById(int id);

}
