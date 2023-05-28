package com.example.polltgbot.services;

import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import com.example.polltgbot.data.repositories.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    public void createAnswer(Answer answer){
        answerRepository.save(answer);
    }
   }
