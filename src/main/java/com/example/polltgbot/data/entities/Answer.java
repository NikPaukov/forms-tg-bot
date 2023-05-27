package com.example.polltgbot.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Answer(Form poll, Message message, User user, String answer) {
        this.poll = poll;
        this.message = message;
        this.user = user;
        this.answer = answer;
    }

    @JoinColumn(name = "poll_id")
    @ManyToOne
    private Form poll;

    @JoinColumn(name = "message_id")
    @ManyToOne
    private Message message;
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    private String answer;

}
