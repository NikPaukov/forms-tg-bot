package com.example.polltgbot.data.repositories;

import com.example.polltgbot.data.entities.Form;
import com.example.polltgbot.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
public Optional<Form> findByCode(String code);
public List<Form> findAllByCreator(User creator);
}
