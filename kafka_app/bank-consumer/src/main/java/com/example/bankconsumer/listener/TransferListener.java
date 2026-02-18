package com.example.bankconsumer.listener;

import com.example.bankconsumer.dto.TransferMessage;
import com.example.bankconsumer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferListener {

    private final TransferService transferService;

    @KafkaListener(
            topics = "${kafka.topic.transfers:bank-transfers}",
            groupId = "${spring.kafka.consumer.group-id:bank-consumer-group}",
            containerFactory = "batchListenerContainerFactory"
    )
    public void consumeBatch(@Payload List<TransferMessage> messages) {
        for (TransferMessage msg : messages) {
            log.info("Starting processing of transfer message: id={}, fromAccountId={}, toAccountId={}, amount={}",
                    msg.getId(), msg.getFromAccountId(), msg.getToAccountId(), msg.getAmount());
            try {
                transferService.processTransfer(msg);
            } catch (Exception e) {
                log.error("Transaction failed for transfer id={}: {}", msg.getId(), e.getMessage());
                transferService.saveFailedTransfer(msg);
            }
        }
    }
}
