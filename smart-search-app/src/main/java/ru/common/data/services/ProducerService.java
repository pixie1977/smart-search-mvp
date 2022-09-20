package ru.common.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.common.data.dto.UserOperationMessage;

@Service
public class ProducerService {

    @Autowired
    private RabbitTemplate template;

    public void broadcastMessage(UserOperationMessage message) {
        try {
            // broadcasts string message to each my-queue-* via my-exchange
            this.template.convertAndSend(
                    "my-exchange",
                    "",
                    new ObjectMapper().writeValueAsString(message)
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}