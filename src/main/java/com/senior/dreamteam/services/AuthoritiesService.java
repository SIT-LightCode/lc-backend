package com.senior.dreamteam.services;

import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Example;
import com.senior.dreamteam.repositories.AuthoritiesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthoritiesService {
    final AuthoritiesRepository authoritiesRepository;

    public List<Authorities> findAll() {
        return authoritiesRepository.findAll();
    }


}
