package com.senior.dreamteam.services;

import com.senior.dreamteam.entities.Lesson;
import com.senior.dreamteam.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    LessonRepository lessonRepository;

    public List<Lesson> findAll(){
        return lessonRepository.findAll();
    }

    public List<Lesson> findAllByTagId(int id){
        return lessonRepository.findAllByTag_Id(id);
    }

    public Lesson upsertLesson(Lesson lesson ){
        return lessonRepository.save(lesson);
    }

    public String removeLessonById(int id){
        try {
            Optional<Lesson> lessonOptional = lessonRepository.findById(id);

            if (lessonOptional.isPresent()) {
                lessonRepository.deleteById(id);
                return "Lesson removed successfully";
            } else {
                return "Lesson not found with ID: " + id;
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }
}
