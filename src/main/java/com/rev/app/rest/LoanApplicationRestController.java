package com.rev.app.rest;

import com.rev.app.dto.LoanApplicationDTO;
import com.rev.app.entity.LoanApplication.LoanStatus;
import com.rev.app.service.ILoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationRestController {

    private final ILoanApplicationService loanApplicationService;

    @Autowired
    public LoanApplicationRestController(ILoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping("/business/{businessUserId}")
    public ResponseEntity<LoanApplicationDTO> applyForLoan(@PathVariable Long businessUserId, @RequestBody LoanApplicationDTO loanDTO) {
        return new ResponseEntity<>(loanApplicationService.applyForLoan(businessUserId, loanDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationDTO> getLoanApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(loanApplicationService.getLoanApplicationById(id));
    }

    @GetMapping("/business/{businessUserId}")
    public ResponseEntity<List<LoanApplicationDTO>> getLoanApplicationsByBusinessUser(@PathVariable Long businessUserId) {
        return ResponseEntity.ok(loanApplicationService.getLoanApplicationsByBusinessUserId(businessUserId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LoanApplicationDTO> updateLoanStatus(@PathVariable Long id, @RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanApplicationService.updateLoanStatus(id, status));
    }

    @GetMapping("/simulate")
    public ResponseEntity<java.util.Map<String, Object>> simulateLoan(
            @RequestParam java.math.BigDecimal principal, 
            @RequestParam Integer tenureMonths, 
            @RequestParam java.math.BigDecimal interestRate) {
        return ResponseEntity.ok(loanApplicationService.simulateLoanRepayment(principal, tenureMonths, interestRate));
    }
}
