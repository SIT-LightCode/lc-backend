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
public class EnumSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String skillName;

//    @OneToOne
//    Skill skill;
}
