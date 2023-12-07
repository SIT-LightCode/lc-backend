package com.senior.dreamteam.jointable.entities.tagproblem.service;

import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.jointable.entities.tagproblem.entity.TagProblem;
import com.senior.dreamteam.jointable.entities.tagproblem.repository.TagProblemRepository;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.repository.TagRepository;
import com.senior.dreamteam.tag.service.TagService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagProblemService {

    @Autowired
    TagService tagService;

    @Autowired
    TagProblemRepository tagProblemRepository;

    public List<TagProblem> findAll() {
        return tagProblemRepository.findAll();
    }

    public Optional<TagProblem> findAllById(int id) {
        return tagProblemRepository.findTagProblemById(id);
    }

    public List<TagProblem> findTagProblemsByTagId(int id) {
        return tagProblemRepository.findTagProblemsByTagId(id);
    }

    public TagProblem upsertTagProblem(TagProblem tagProblem) {
        return tagProblemRepository.save(tagProblem);
    }

    public Boolean upsertMultiTagProblemByProblemAndArrTagId(Problem problem, String arrayTagId) throws JSONException {
        try {
            JSONArray tagIds = new JSONArray(arrayTagId);
            List<TagProblem> tagProblems = new ArrayList<>();
            for (int i = 0; i < tagIds.length(); i++) {
                int tagId = tagIds.getInt(i); // Assuming the array contains integers
                Tag tag = tagService.findAllById(tagId).orElse(null);
                if (tag != null) {
                    TagProblem tagProblem = new TagProblem();
                    tagProblem.setProblem(problem);
                    tagProblem.setTag(tag);
                    tagProblems.add(tagProblem);
                }
            }
            tagProblemRepository.saveAll(tagProblems); // Save all entities at once
            return true;
        } catch (Exception e) {
            throw new DemoGraphqlException("An error occured: " + e.getMessage(), 400);
        }
    }

    public String removeTagProblemById(int id) {
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
