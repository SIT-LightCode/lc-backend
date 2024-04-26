package com.senior.dreamteam.repositories;


import com.senior.dreamteam.entities.Authorities;
import com.senior.dreamteam.entities.Roles;
import com.senior.dreamteam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(int id);
    Authorities findByName(Roles name);

    @Query(value = "select id, name, email, score from ( select u.id, u.name, u.email from user u ) uu join (\n" +
            "select sum(s.score) as score, s.user_id from submission s group by s.user_id\n" +
            ") ss\n" +
            "on uu.id = ss.user_id order by score desc", nativeQuery = true)
    List<Object[]> findUserDetailsWithSubmissions();
}
