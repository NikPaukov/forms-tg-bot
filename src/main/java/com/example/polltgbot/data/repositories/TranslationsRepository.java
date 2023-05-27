package com.example.polltgbot.data.repositories;

import com.example.polltgbot.data.entities.Translation;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationsRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findByNameAndLanguage(String name, String language);
}
