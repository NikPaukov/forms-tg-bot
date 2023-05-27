package com.example.polltgbot.data.repositories;

import com.example.polltgbot.data.entities.Answer;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    boolean existsAnswerByPollAndUser(Form poll, User user);
}
