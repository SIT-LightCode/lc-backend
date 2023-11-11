package com.senior.dreamteam.lesson.repository;


import com.senior.dreamteam.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findAll();
    List<Lesson> findAllByTag_Id(Long tagId);
}
