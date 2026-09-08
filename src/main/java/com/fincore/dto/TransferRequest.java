package com.fincore.dto;
import java.math.BigDecimal;

public record TransferRequest(String sourceAccountId, String destinationAccountId, BigDecimal amount) {}