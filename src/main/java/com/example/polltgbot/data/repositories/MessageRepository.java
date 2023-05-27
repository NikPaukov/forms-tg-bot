package com.example.polltgbot.data.repositories;

import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
List<Message> findAllByPollOrderBySendOrder(Form poll);
List<Message> findAllByAnswerAndPoll(Form poll, String answer);
}
