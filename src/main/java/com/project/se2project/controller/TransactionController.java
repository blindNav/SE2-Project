package com.project.se2project.controller;

import com.project.se2project.domain.Transaction.CreateTransactionResponse;
import com.project.se2project.domain.Transaction.GetAllTransactionResponse;
import com.project.se2project.domain.Transaction.GetTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.project.se2project.domain.Transaction.CreateTransactionRequest;
import com.project.se2project.model.Transaction;
import com.project.se2project.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@RequestMapping(value = "/transaction/")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping(value = "/")
    public ResponseEntity<CreateTransactionResponse> createTransaction(@CookieValue(name = "jwt", defaultValue = "dark") String jwt, @Valid @RequestBody CreateTransactionRequest createTransactionRequest) {
        try {

            Long toUserId = transactionService.getIdByUsername(createTransactionRequest.getToUserUsername());
            Long fromUserId = transactionService.getIdByUsername(createTransactionRequest.getFromUserUsername());
            if (toUserId == null || fromUserId == null) {
                throw new Exception("User not found");
            }
            Transaction transaction = transactionService.createTransaction(fromUserId, toUserId, createTransactionRequest.getAmount(),createTransactionRequest.getTransactionTime(), jwt);

            CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse("Create transaction successfully");
            return ResponseEntity.status(HttpStatus.OK).body(createTransactionResponse);
        } catch (Exception e) {
            CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createTransactionResponse);
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<GetTransactionResponse> getTransaction(@CookieValue(name = "jwt", defaultValue = "dark") String jwt, @PathVariable int id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id, jwt);
            GetTransactionResponse getTransactionResponse = new GetTransactionResponse(transaction);
            return ResponseEntity.status(HttpStatus.OK).body(new GetTransactionResponse(transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new GetTransactionResponse(e.getMessage()));
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<GetAllTransactionResponse> getAllTransactions(@CookieValue(name = "jwt", defaultValue = "dark") String jwt) {
        try {
            List<Transaction> allTransaction = transactionService.getAllTransactions(jwt);
            List<GetTransactionResponse> getTransactionResponseList = new ArrayList<>();
            allTransaction.forEach(transaction -> {
                getTransactionResponseList.add(new GetTransactionResponse(transaction));
            });
            return ResponseEntity.status(HttpStatus.OK).body(new GetAllTransactionResponse(getTransactionResponseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new GetAllTransactionResponse(e.getMessage()));
        }
    }

    @GetMapping(value = "/{page}")
    public ResponseEntity<GetAllTransactionResponse> getAllTransactionPerPage(@PathVariable int page, @CookieValue(name = "jwt", defaultValue = "dark") String jwt) {
        try {
            List<Transaction> transactionList = transactionService.getAllTransactionsPerPage(page);
            if (transactionList == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new GetAllTransactionResponse("Out of range"));
            }
            List<GetTransactionResponse> getTransactionResponseList = new ArrayList<>();
            transactionList.forEach(transaction -> {
                getTransactionResponseList.add(new GetTransactionResponse(transaction));
            });
            return ResponseEntity.status(HttpStatus.OK).body(new GetAllTransactionResponse(getTransactionResponseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new GetAllTransactionResponse(e.getMessage()));
        }
    }

    @GetMapping(value = "/user/{username}")
    public ResponseEntity<GetAllTransactionResponse> getAllTransactionsByUsername(@PathVariable String username, @CookieValue(name = "jwt", defaultValue = "dark") String jwt) {
        try {

            Long userId = transactionService.getUserIdByUsername(username);
            List<Transaction> transactionList = transactionService.getAllTransactionsByUserId(userId, jwt);
            List<GetTransactionResponse> getTransactionResponseList = new ArrayList<>();
            transactionList.forEach(transaction -> {
                getTransactionResponseList.add(new GetTransactionResponse(transaction));
            });
            return ResponseEntity.status(HttpStatus.OK).body(new GetAllTransactionResponse(getTransactionResponseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new GetAllTransactionResponse(e.getMessage()));
        }
    }
}
