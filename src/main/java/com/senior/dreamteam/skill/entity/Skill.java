package com.senior.dreamteam.skill.entity;

import com.senior.dreamteam.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    EnumSkill enumSkill;

    Boolean isPassed;

    @ManyToOne
    User user;
}
