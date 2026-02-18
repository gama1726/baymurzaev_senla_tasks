package com.example.bankproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.bankproducer.service.AccountInitService;

@SpringBootApplication
@EnableScheduling
public class BankProducerApplication {

    private final AccountInitService accountInitService;

    public BankProducerApplication(AccountInitService accountInitService) {
        this.accountInitService = accountInitService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankProducerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        accountInitService.initAccounts();
    }
}
