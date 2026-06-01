package com.example.tracker.repositories;

import com.example.tracker.entity.Expense;
import com.example.tracker.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {


    Optional<Expense> findById(Long id);

    List<Expense> findByDate(LocalDate date);

    List<Expense> findByType(ExpenseType type);

    List<Expense> findByDateBetween(LocalDate start, LocalDate end);

    @Query("select sum(e.amount) from Expense e where e.type=:type ")
    Double sumByType(@Param("type") ExpenseType type);

}
