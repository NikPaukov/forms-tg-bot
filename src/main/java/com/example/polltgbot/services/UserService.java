package com.example.polltgbot.services;

import com.example.polltgbot.Exceptions.UserAlreadyExistsException;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
private final UserRepository userRepository;

public Optional<User> getUser(Long userid){
    return userRepository.findByUserid(userid);
}
@Transactional
public User createUser(Long userid, String name, String surname, String tag){
    if(userRepository.findByUserid(userid).isPresent()){
        throw new UserAlreadyExistsException();
    }
    User user = new User(userid, "ua", Timestamp.from(Instant.now()), name, surname, tag);
    userRepository.save(user);
    return user;
}

}
