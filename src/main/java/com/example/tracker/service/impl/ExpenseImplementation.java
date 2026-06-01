package com.example.tracker.service.impl;

import com.example.tracker.entity.Expense;
import com.example.tracker.enums.ExpenseCategory;
import com.example.tracker.enums.ExpenseType;
import com.example.tracker.exceptions.NotFoundException;
import com.example.tracker.mapper.ExpenseMapper;
import com.example.tracker.model.request.ExpenseRequest;
import com.example.tracker.model.response.BalanceResponse;
import com.example.tracker.model.response.ExpenseResponse;
import com.example.tracker.repositories.ExpenseRepository;
import com.example.tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseImplementation implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final  ExpenseMapper expenseMapper;
    private final MlCategoryService mlCategoryService;



    @Override
    public ExpenseResponse save(ExpenseRequest request) {
        Expense expense = expenseMapper.toEntity(request);


        if (expense.getCategory() == null) {
            ExpenseCategory predicted = mlCategoryService.predict(request.getTitle());
            expense.setCategory(predicted);
        }

        Expense saved = expenseRepository.save(expense);

        return expenseMapper.toResponse(saved);
    }

    @Override
    public ExpenseResponse findById(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(()->
                new NotFoundException("Expense is not found!"));
        return expenseMapper.toResponse(expense);
    }

    @Override
    public List<ExpenseResponse> findByDate(LocalDate date) {
        List<Expense> expenses = expenseRepository.findByDate(date);
        return expenses.stream().map(expenseMapper::toResponse).toList();
    }


    @Override
    public List<ExpenseResponse> findAll() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    public ExpenseResponse update(Long id, ExpenseRequest expenseRequest) {
        Expense expense = expenseRepository.findById(id).orElseThrow(()->
                new NotFoundException("Not found expense!"));

        expenseMapper.updateEntity(expenseRequest,expense);

        if (expense.getCategory() == null) {
            ExpenseCategory predicted = mlCategoryService.predict(expenseRequest.getTitle());
            expense.setCategory(predicted);
        }

        Expense saved =  expenseRepository.save(expense);

        return expenseMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(()->new NotFoundException("Expense not found"));
         expenseRepository.delete(expense);

    }

    @Override
    public BalanceResponse getSummary(){
        Double income = expenseRepository.sumByType(ExpenseType.INCOME);
        Double expense = expenseRepository.sumByType(ExpenseType.EXPENSE);

        income = income==null? 0.0: income;
        expense = expense==null? 0.0:expense;

        BalanceResponse response = new BalanceResponse();

        response.setIncome(income);
        response.setExpense(expense);
        response.setBalance(income-expense);

        return response;

    }

    @Override
    public Double getTotalExpenseBetween(LocalDate start, LocalDate end) {

        if(start.isAfter(end)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<Expense> list =expenseRepository.findByDateBetween(start, end);

        return list.stream().filter(e->e.getType()==ExpenseType.EXPENSE)
                .mapToDouble(Expense::getAmount).sum();
    }
}
