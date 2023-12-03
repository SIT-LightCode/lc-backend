package com.senior.dreamteam.jointable.entities.tagproblem.service;

import com.senior.dreamteam.jointable.entities.tagproblem.entity.TagProblem;
import com.senior.dreamteam.jointable.entities.tagproblem.repository.TagProblemRepository;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagProblemService {

    @Autowired
    TagProblemRepository tagProblemRepository;

    public List<TagProblem> findAll(){
        return tagProblemRepository.findAll();
    }

    public Optional<TagProblem> findAllById(int id){
        return tagProblemRepository.findTagProblemById(id);
    }

    public TagProblem upsertTagProblem(TagProblem tagProblem ){
        return tagProblemRepository.save(tagProblem);
    }

    public String removeTagById(int id){
        try {
            Optional<TagProblem> tagProblemOptional = tagProblemRepository.findById(id);

            if (tagProblemOptional.isPresent()) {
                tagProblemRepository.deleteById(id);
                return "TagProblem removed successfully";
            } else {
                return "TagProblem not found with ID: " + id;
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }
}
