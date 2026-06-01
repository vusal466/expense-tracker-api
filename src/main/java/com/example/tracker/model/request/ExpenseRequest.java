package com.example.tracker.model.request;


import com.example.tracker.enums.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExpenseRequest {

    @NotBlank
    private String title;

    @Positive
    private Double amount;

    private ExpenseType type;



}
