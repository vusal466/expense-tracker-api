package com.example.tracker.service;


import com.example.tracker.model.request.ExpenseRequest;
import com.example.tracker.model.response.BalanceResponse;
import com.example.tracker.model.response.ExpenseResponse;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    BalanceResponse getSummary();

    Double getTotalExpenseBetween(LocalDate start, LocalDate end);

    ExpenseResponse save(ExpenseRequest request);

    ExpenseResponse findById(Long id);

    List<ExpenseResponse> findByDate(LocalDate date);

    List<ExpenseResponse> findAll();

    ExpenseResponse update(Long id, ExpenseRequest expenseRequest);

    void delete(Long id);


}
