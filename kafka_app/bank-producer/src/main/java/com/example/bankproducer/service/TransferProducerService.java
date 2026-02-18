package com.example.bankproducer.service;

import com.example.bankproducer.dto.TransferMessage;
import com.example.bankproducer.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferProducerService {

    private static final int MESSAGES_PER_SECOND = 5;
    private static final Random RANDOM = new Random();

    private final KafkaTemplate<String, TransferMessage> kafkaTemplate;
    private final AccountInitService accountInitService;

    @Value("${kafka.topic.transfers:bank-transfers}")
    private String topicName;

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    public void produceTransferMessages() {
        List<Account> accounts = accountInitService.getAccountList();
        if (accounts.size() < 2) {
            log.warn("Not enough accounts to produce transfers (need at least 2)");
            return;
        }

        kafkaTemplate.executeInTransaction(operations -> {
            for (int i = 0; i < MESSAGES_PER_SECOND; i++) {
                int fromIdx = RANDOM.nextInt(accounts.size());
                int toIdx = RANDOM.nextInt(accounts.size());
                while (toIdx == fromIdx) {
                    toIdx = RANDOM.nextInt(accounts.size());
                }
                Account from = accounts.get(fromIdx);
                Account to = accounts.get(toIdx);

                BigDecimal amount = BigDecimal.valueOf(1 + RANDOM.nextDouble() * 999)
                        .setScale(2, RoundingMode.HALF_UP);
                UUID transferId = UUID.randomUUID();

                TransferMessage message = TransferMessage.builder()
                        .id(transferId)
                        .fromAccountId(from.getId())
                        .toAccountId(to.getId())
                        .amount(amount)
                        .build();

                String key = transferId.toString();
                operations.send(topicName, key, message);
                log.info("Sent transfer message: id={}, fromAccountId={}, toAccountId={}, amount={}",
                        transferId, from.getId(), to.getId(), amount);
            }
            return null;
        });
    }
}
