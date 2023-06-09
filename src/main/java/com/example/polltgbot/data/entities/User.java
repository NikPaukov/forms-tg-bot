package com.example.polltgbot.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    public User( Long userid, String language, Timestamp registeredAt, String name, String surname, String username ) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.userid = userid;
        this.language = language;
        this.registeredAt = registeredAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String username;

    private Long userid;

    private String language;

    @Column(name="registered_at")
    private Timestamp registeredAt;

}
