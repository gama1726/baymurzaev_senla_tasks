package com.example.bankproducer.service;

import com.example.bankproducer.entity.Account;
import com.example.bankproducer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountInitService {

    private static final int INITIAL_ACCOUNT_COUNT = 1000;
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100000.00");

    private final AccountRepository accountRepository;

    private final Map<Long, Account> accountsMap = new ConcurrentHashMap<>();

    @Transactional
    public void initAccounts() {
        if (accountRepository.count() == 0) {
            log.info("Accounts table is empty, generating {} accounts", INITIAL_ACCOUNT_COUNT);
            for (int i = 0; i < INITIAL_ACCOUNT_COUNT; i++) {
                Account account = Account.builder()
                        .balance(INITIAL_BALANCE)
                        .build();
                account = accountRepository.save(account);
                accountsMap.put(account.getId(), account);
            }
            log.info("Generated and saved {} accounts to DB and local Map", INITIAL_ACCOUNT_COUNT);
        } else {
            log.info("Loading existing accounts from DB into local Map");
            List<Account> accounts = accountRepository.findAll();
            accounts.forEach(a -> accountsMap.put(a.getId(), a));
            log.info("Loaded {} accounts into local Map", accountsMap.size());
        }
    }

    public Map<Long, Account> getAccountsMap() {
        return accountsMap;
    }

    public List<Account> getAccountList() {
        return List.copyOf(accountsMap.values());
    }
}
