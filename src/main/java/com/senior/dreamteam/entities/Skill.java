package com.senior.dreamteam.entities;

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

    //    @OneToOne
    //    EnumSkill enumSkill;
    @ManyToOne
    Tag tag;

    Integer level;

    @ManyToOne
    User user;
}
