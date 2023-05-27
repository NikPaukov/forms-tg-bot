package com.example.polltgbot.data.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="translations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;

    public Translation(String language, String name, String value) {
        this.language = language;
        this.name = name;
        this.value = value;
    }

    private String name;
    private String value;
}
