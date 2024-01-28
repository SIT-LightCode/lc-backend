package com.senior.dreamteam.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String name;

    String email;

    String password;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Authorities> authorities;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Submission> submission;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Problem> problem;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    List<Problem> likedProblem;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Skill> skill;

    @Builder.Default
    Date lastPasswordReset = Date.from(LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @Builder.Default
    Boolean enables = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities
                .stream()
                .map(authorities -> new SimpleGrantedAuthority(authorities.getName().name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public List<String> getSimpleAuthorities() {
        return this.authorities.stream().map(authorities -> authorities.getName().name()).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enables;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enables;
    }
}

