package com.example.tracker.entity;

import com.example.tracker.enums.ExpenseCategory;
import com.example.tracker.enums.ExpenseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name="expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Positive
    private Double amount;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private LocalDate date = LocalDate.now();
}
