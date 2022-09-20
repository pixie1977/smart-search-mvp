package ru.common.search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import ru.common.data.dto.UserOperationCode;
import ru.common.data.dto.UserOperationMessage;
import ru.common.search.index.IndexLoaderService;

import java.util.Map;

@Service
public class ConsumerService {

    private final IndexLoaderService indexLoaderService;

    @Autowired
    public ConsumerService(IndexLoaderService indexLoaderService){
        this.indexLoaderService = indexLoaderService;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(ConsumerService consumerService) {
        return new MessageListenerAdapter(consumerService, "handleMessage");
    }

    public void handleMessage(String message) {
        try {
            UserOperationMessage userOperationMessage = new ObjectMapper().readValue(
                    message, UserOperationMessage.class);
            if(userOperationMessage.getCode() == UserOperationCode.ADD){
                Map userAsProps = new ObjectMapper().convertValue(userOperationMessage.getUser(), Map.class);
                indexLoaderService.loadMapToIndex(userAsProps);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}