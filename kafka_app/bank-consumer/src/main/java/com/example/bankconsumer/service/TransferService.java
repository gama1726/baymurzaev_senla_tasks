package com.example.bankconsumer.service;

import com.example.bankconsumer.dto.TransferMessage;
import com.example.bankconsumer.entity.Account;
import com.example.bankconsumer.entity.Transfer;
import com.example.bankconsumer.repository.AccountRepository;
import com.example.bankconsumer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveFailedTransfer(TransferMessage msg) {
        transferRepository.save(Transfer.builder()
                .id(msg.getId())
                .fromAccountId(msg.getFromAccountId())
                .toAccountId(msg.getToAccountId())
                .amount(msg.getAmount())
                .status(Transfer.TransferStatus.FAILED)
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void processTransfer(TransferMessage msg) {
        Long fromId = msg.getFromAccountId();
        Long toId = msg.getToAccountId();
        Account from;
        Account to;
        if (fromId.compareTo(toId) <= 0) {
            from = accountRepository.findByIdForUpdate(fromId).orElse(null);
            to = accountRepository.findByIdForUpdate(toId).orElse(null);
        } else {
            to = accountRepository.findByIdForUpdate(toId).orElse(null);
            from = accountRepository.findByIdForUpdate(fromId).orElse(null);
        }

        if (from == null || to == null) {
            log.error("Validation failed: account(s) not found. Transfer id={}, fromAccountId={}, toAccountId={}",
                    msg.getId(), msg.getFromAccountId(), msg.getToAccountId());
            return;
        }

        if (from.getBalance().compareTo(msg.getAmount()) < 0) {
            log.error("Validation failed: insufficient balance. Transfer id={}, fromAccountId={}, balance={}, amount={}",
                    msg.getId(), msg.getFromAccountId(), from.getBalance(), msg.getAmount());
            return;
        }

        from.setBalance(from.getBalance().subtract(msg.getAmount()));
        to.setBalance(to.getBalance().add(msg.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        transferRepository.save(Transfer.builder()
                .id(msg.getId())
                .fromAccountId(msg.getFromAccountId())
                .toAccountId(msg.getToAccountId())
                .amount(msg.getAmount())
                .status(Transfer.TransferStatus.READY)
                .build());
        log.info("Transfer processed successfully: id={}", msg.getId());
    }
}
