package com.fincore.dto;
import java.math.BigDecimal;

public record DepositRequest(String accountId, BigDecimal amount) {}