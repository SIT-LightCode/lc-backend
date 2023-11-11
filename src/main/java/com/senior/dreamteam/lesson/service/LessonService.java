package com.senior.dreamteam.lesson.service;

import com.senior.dreamteam.lesson.entity.Lesson;
import com.senior.dreamteam.lesson.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    LessonRepository lessonRepository;

    public List<Lesson> findAll(){
        return lessonRepository.findAll();
    }

    public List<Lesson> findAllByTagId(Long id){
        return lessonRepository.findAllByTag_Id(id);
    }
}
