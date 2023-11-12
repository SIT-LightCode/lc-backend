package com.senior.dreamteam.lesson.repository;


import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.lesson.entity.LessonInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findAll();
    List<Lesson> findAllByTag_Id(int tagId);
    Optional<Lesson> findById(int id);

}
