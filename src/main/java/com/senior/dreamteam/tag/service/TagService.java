package com.senior.dreamteam.tag.service;

import com.senior.dreamteam.tag.entity.Tag;
import com.senior.dreamteam.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    TagRepository tagRepository;

    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    public Optional<Tag> findAllById(int id){
        return tagRepository.findTagById(id);
    }
}
