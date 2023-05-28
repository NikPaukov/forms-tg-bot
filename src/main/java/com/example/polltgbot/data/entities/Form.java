package com.example.polltgbot.data.entities;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "forms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;

    @ManyToOne
    @JoinColumn(name="creator_id")
    private User creator;

    private Timestamp createdAt;

    private Timestamp expiresAt;

    public Form(String code, User creator, Timestamp createdAt, Timestamp expiresAt, String name) {
        this.name = name;
        this.code = code;
        this.creator = creator;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
