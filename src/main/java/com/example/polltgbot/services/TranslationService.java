package com.example.polltgbot.services;

import com.example.polltgbot.Exceptions.TranslationNotFoundException;
import com.example.polltgbot.data.entities.Translation;
import com.example.polltgbot.data.repositories.TranslationsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class TranslationService {
private final TranslationsRepository translationsRepository;

public Translation getTranslation(String language, String name){
    return translationsRepository.findByNameAndLanguage(name, language)
            .orElseGet(()->new Translation(language, name, "none"));
}

}
