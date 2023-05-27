package com.example.polltgbot.utils.Serializers.Deserializers;

import com.example.polltgbot.data.PollDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegularPollMessageDeserializer implements MessageDeserializer<PollDTO> {
    private final ObjectMapper objectMapper;
    @Override
    public PollDTO deserialize(String obj) throws JsonProcessingException {
        return objectMapper.readValue(obj, PollDTO.class);
    }
}
