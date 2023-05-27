package com.example.polltgbot.utils.Serializers.Deserializers;

import org.springframework.stereotype.Component;

@Component
public class TextMessageDeserializer implements MessageDeserializer<String>{
    @Override
    public String deserialize(String obj) {
        return obj;
    }
}
