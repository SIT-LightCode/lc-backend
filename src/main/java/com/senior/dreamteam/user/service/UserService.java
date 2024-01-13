package com.senior.dreamteam.user.service;

import com.senior.dreamteam.exception.DemoGraphqlException;
import com.senior.dreamteam.problem.entity.Problem;
import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.service.TagService;
import com.senior.dreamteam.tagproblem.entity.TagProblem;
import com.senior.dreamteam.tagproblem.repository.TagProblemRepository;
import com.senior.dreamteam.user.entity.User;
import com.senior.dreamteam.user.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    TagService tagService;

    @Autowired
    TagProblemRepository tagProblemRepository;

    @Autowired
    UserRepository userRepository;

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
            tagProblemRepository.removeTagProblemsByProblemId(problem.getId());
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
            throw new DemoGraphqlException("An error occured: " + e.getMessage());
        }
    }

    public String removeUserById(int id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                userRepository.deleteById(id);
                return "User removed successfully";
            } else {
                return "User not found with ID: " + id;
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

}
