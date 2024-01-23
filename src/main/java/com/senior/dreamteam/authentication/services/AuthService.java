//package com.senior.dreamteam.authentication;
//
//import com.senior.dreamteam.entities.Authorities;
//import com.senior.dreamteam.entities.Roles;
//import com.senior.dreamteam.entities.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    final AuthDao authDao;
//    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    public User getUserByUsername(String username) {
//        return authDao.getUserByUsername(username);
//    }
//
//    public User getUserByEmail(String email) {
//        return authDao.getUserByEmail(email);
//    }
//
//    public User getUserById(UUID id) {
//        return authDao.getUserById(id);
//    }
//
//
//
//    @Override
//    public Users updateUser(UUID userId, UpdateRequest user) {
//        Users oldUser = authDao.getUserById(userId);
//        Location location = oldUser.getLocation();
//        location.setAddress(user.address());
//        location.setCity(user.city());
//        location.setState(user.state());
//        location.setCountry(user.country());
//        location.setZip(user.zip());
//        authDao.saveLocation(location);
//
//        oldUser.setProfilePicture(user.profilePicture());
//        oldUser.setPlatformName(user.platformName());
//        oldUser.setFirstName(user.firstName());
//        oldUser.setLastName(user.lastName());
//        oldUser.setTel(user.tel());
//        oldUser.setBirthday(user.birthday());
//        oldUser.setLocation(location);
//
//        return authDao.saveUser(oldUser);
//    }
//}