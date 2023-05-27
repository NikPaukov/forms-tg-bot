package com.example.polltgbot.utils.Serializers.Deserializers;

import com.example.polltgbot.data.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageDeserializersHandler {
private final RegularPollMessageDeserializer regularPollMessageDeserializer;
private final TextMessageDeserializer textMessageDeserializer;

public MessageDeserializer getMessageSerializer(MessageTypeEnum type){
    if (type.equals(MessageTypeEnum.TEXT)) {
        return textMessageDeserializer;
    } else if (type.equals(MessageTypeEnum.POLL)) {
        return regularPollMessageDeserializer;
    }
    return textMessageDeserializer;
}

}
