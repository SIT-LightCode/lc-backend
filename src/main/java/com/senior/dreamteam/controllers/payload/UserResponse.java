package com.senior.dreamteam.controllers.payload;

import com.senior.dreamteam.entities.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private List<String> authorities;
    private int score;
    private int scoreUnOfficial;
    private List<Skill> skills;
}