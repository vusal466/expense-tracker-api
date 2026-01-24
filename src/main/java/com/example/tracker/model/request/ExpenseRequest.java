package com.example.tracker.model.request;

import com.example.tracker.enums.ExpenseCategory;
import com.example.tracker.enums.ExpenseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

    @NotBlank
    private String title;

    @Positive
    private Double amount;

    private ExpenseType type;

    private ExpenseCategory category;


}
