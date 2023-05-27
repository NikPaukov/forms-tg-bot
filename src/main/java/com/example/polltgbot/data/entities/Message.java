package com.example.polltgbot.data.entities;

import com.example.polltgbot.data.enums.MessageTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Message {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    public Message(Form poll, Integer sendOrder, MessageTypeEnum messageType, String answer) {

        this.poll = poll;
        this.sendOrder = sendOrder;
        this.messageType = messageType;
        this.answer = answer;
    }

    @JoinColumn(name = "poll_id")
@ManyToOne
private Form poll;

@Column(name = "send_order")
private Integer sendOrder;

@Column(name = "type")
private MessageTypeEnum messageType;

private String answer;

}
