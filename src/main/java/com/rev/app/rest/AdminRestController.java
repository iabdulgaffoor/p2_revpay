package com.rev.app.rest;

import com.rev.app.dto.UserDTO;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.ITransactionRepository;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminRestController {

    private final IUserRepository userRepository;
    private final ITransactionRepository transactionRepository;
    private final UserMapper userMapper;
    private final com.rev.app.service.IWalletService walletService;
    private final com.rev.app.service.ILoanApplicationService loanService;
    private final com.rev.app.service.IUserService userService;

    @Autowired
    public AdminRestController(IUserRepository userRepository, 
                               ITransactionRepository transactionRepository,
                               UserMapper userMapper,
                               com.rev.app.service.IWalletService walletService,
                               com.rev.app.service.ILoanApplicationService loanService,
                               com.rev.app.service.IUserService userService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.userMapper = userMapper;
        this.walletService = walletService;
        this.loanService = loanService;
        this.userService = userService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        long totalUsers = userRepository.count();
        BigDecimal totalVolume = transactionRepository.getTotalTransactionVolume();
        long totalTransactions = transactionRepository.count();

        metrics.put("totalUsers", totalUsers);
        metrics.put("totalVolume", totalVolume);
        metrics.put("totalTransactions", totalTransactions);
        metrics.put("systemStatus", "Healthy");

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/users")
    public ResponseEntity<org.springframework.data.domain.Page<UserDTO>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String search) {
        return ResponseEntity.ok(userService.getAllUsersPaginated(page, size, sortBy, sortDir, search));
    }

    @PostMapping("/users/{userId}/balance/add")
    public ResponseEntity<com.rev.app.dto.WalletDTO> addFunds(@PathVariable Long userId, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        return ResponseEntity.ok(walletService.adminAddFunds(userId, amount));
    }

    @PostMapping("/users/{userId}/balance/deduct")
    public ResponseEntity<com.rev.app.dto.WalletDTO> deductFunds(@PathVariable Long userId, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        return ResponseEntity.ok(walletService.adminDeductFunds(userId, amount));
    }

    @GetMapping("/loans")
    public ResponseEntity<List<com.rev.app.dto.LoanApplicationDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoanApplications());
    }

    @PutMapping("/loans/{id}/status")
    public ResponseEntity<com.rev.app.dto.LoanApplicationDTO> updateLoanStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        com.rev.app.entity.LoanApplication.LoanStatus status = com.rev.app.entity.LoanApplication.LoanStatus.valueOf(request.get("status").toUpperCase());
        return ResponseEntity.ok(loanService.updateLoanStatus(id, status));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody Map<String, Object> request) {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = mapper.convertValue(request.get("user"), UserDTO.class);
        String password = (String) request.get("password");
        com.rev.app.entity.User.Role role = com.rev.app.entity.User.Role.valueOf(((String) request.get("role")).toUpperCase());
        
        if (request.containsKey("isActive")) {
            userDTO.setIsActive((Boolean) request.get("isActive"));
        }
        
        return ResponseEntity.ok(userService.adminCreateUser(userDTO, password, role));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO = mapper.convertValue(request.get("user"), UserDTO.class);
        String newPassword = (String) request.get("newPassword");
        com.rev.app.entity.User.Role role = com.rev.app.entity.User.Role.valueOf(((String) request.get("role")).toUpperCase());
        
        boolean isActive = request.containsKey("isActive") ? (Boolean) request.get("isActive") : true;
        userDTO.setIsActive(isActive);

        return ResponseEntity.ok(userService.adminUpdateUser(id, userDTO, newPassword, role, isActive));
    }
}
