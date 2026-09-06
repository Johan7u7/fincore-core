package com.fincore.controller;

import com.fincore.LedgerService;
import com.fincore.Transaction;
import com.fincore.dto.DepositRequest;
import com.fincore.dto.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier frontend local
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String id) {
        BigDecimal balance = ledgerService.getBalance(id);
        return ResponseEntity.ok(Map.of("accountId", id, "balance", balance));
    }

    @GetMapping("/accounts/{id}/history")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable String id) {
        return ResponseEntity.ok(ledgerService.getHistory(id));
    }

    @PostMapping("/transactions/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody DepositRequest request) {
        Transaction tx = ledgerService.deposit(request.getAccountId(), request.getAmount());
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequest request) {
        ledgerService.transfer(
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getAmount());
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Transfer executed successfully"));
    }
}