package com.example.polltgbot.utils.Serializers.Deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

@Component
public interface MessageDeserializer<T> {
public T deserialize(String obj) throws JsonProcessingException;
}
