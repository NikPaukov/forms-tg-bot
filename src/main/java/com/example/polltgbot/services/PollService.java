package com.example.polltgbot.services;

import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.repositories.FormRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PollService {
private final FormRepository pollRepository;

public void createForm(Form poll){
    pollRepository.save(poll);
}
public Form getFormByCode(String code) {
    return pollRepository.findByCode(code).orElseThrow(IllegalArgumentException::new);
}

    public List<Form> getFormsByCreator(User user){
        return pollRepository.findAllByCreator(user);
    }
}
