package com.example.tracker.model.response;

import lombok.Data;

@Data
public class BalanceResponse {
    private Double income;
    private Double expense;
    private Double balance;
}
