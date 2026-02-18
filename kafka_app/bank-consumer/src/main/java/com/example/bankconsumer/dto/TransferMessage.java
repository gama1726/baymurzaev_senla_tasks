package com.example.bankconsumer.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMessage {

    private UUID id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
