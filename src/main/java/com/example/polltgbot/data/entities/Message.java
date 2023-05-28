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

    public Message(Form form, Integer sendOrder, MessageTypeEnum messageType, String data) {

        this.form = form;
        this.sendOrder = sendOrder;
        this.messageType = messageType;
        this.data = data;
    }

    @JoinColumn(name = "form_id")
    @ManyToOne
    private Form form;

    @Column(name = "send_order")
    private Integer sendOrder;

    @Column(name = "message_type")
    private MessageTypeEnum messageType;

    private String data;

}
