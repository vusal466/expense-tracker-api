package com.example.tracker.model.response;

import com.example.tracker.enums.ExpenseCategory;
import com.example.tracker.enums.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponse {

    private Long id;
    private String title;
    private Double amount;
    private ExpenseType type;
    private ExpenseCategory category;
    private LocalDate date;

}
