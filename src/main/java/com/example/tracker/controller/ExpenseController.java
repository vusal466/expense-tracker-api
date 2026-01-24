package com.example.tracker.controller;
import com.example.tracker.model.request.ExpenseRequest;
import com.example.tracker.model.response.BalanceResponse;
import com.example.tracker.model.response.ExpenseResponse;
import com.example.tracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Expense API", description = "Expense management operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Operation(summary = "Get all expenses")
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> findAll(){
        List<ExpenseResponse> response = expenseService.findAll();
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get expense by id")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> findById(@PathVariable Long id){
        ExpenseResponse response = expenseService.findById(id);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Create new expense")
    @PostMapping("/save")
    public ResponseEntity<ExpenseResponse> save(@RequestBody @Valid ExpenseRequest request){
        ExpenseResponse response = expenseService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update expense")
    @PutMapping("/update/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,
                                                  @RequestBody @Valid ExpenseRequest request){
        ExpenseResponse response = expenseService.update(id,request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete expense by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get balance summary")
    @GetMapping("/summary")
    public ResponseEntity<BalanceResponse> getSummary(){
        return ResponseEntity.ok(expenseService.getSummary());
    }


    @Operation(summary = "Get total expense between two dates")
    @GetMapping("/expenserange")
    public ResponseEntity<Double> getTotalExpenseBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end){
        return ResponseEntity.ok(expenseService.getTotalExpenseBetween(start, end));
    }


}
