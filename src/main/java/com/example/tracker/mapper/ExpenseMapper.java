package com.example.tracker.mapper;

import com.example.tracker.entity.Expense;
import com.example.tracker.model.request.ExpenseRequest;
import com.example.tracker.model.response.ExpenseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    public abstract Expense toEntity(ExpenseRequest expenseRequest);

    public abstract ExpenseResponse toResponse(Expense expense);

    public abstract void updateEntity(ExpenseRequest request, @MappingTarget Expense expense);
}
