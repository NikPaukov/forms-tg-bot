package com.example.polltgbot.services;

import com.example.polltgbot.data.entities.Message;
import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.repositories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
private final MessageRepository messageRepository;

public void createMessage(Message message){
    messageRepository.save(message);
}
public void createMessages(List<Message> messageList){
    messageRepository.saveAll(messageList);
}
public List<Message> findAllByPoll(Form poll){
    return messageRepository.findAllByFormOrderBySendOrder(poll);
}
public Message findById(Long id){
    return messageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
}
}
