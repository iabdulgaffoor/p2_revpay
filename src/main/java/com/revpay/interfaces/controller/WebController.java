package com.revpay.interfaces.controller;

import com.revpay.application.dto.*;
import com.revpay.application.service.*;
import com.revpay.domain.model.*;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final IAuthService authService;
    private final IUserRepo userRepo;
    private final IWalletRepo walletRepo;
    private final ITransactionRepo transactionRepo;
    private final ITransactionService transactionService;
    private final IAdminService adminService;
    private final IBusinessService businessService;
    private final IMoneyRequestService moneyRequestService;
    private final IMoneyRequestRepo moneyRequestRepo;
    private final ILoanRepo loanRepo;
    private final IBusinessProfileRepo businessProfileRepo;
    private final IInvoiceService invoiceService;
    private final INotificationRepo notificationRepo;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequestDto request) {
        authService.register(request);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepo.findByEmailIgnoreCase(email).orElse(null);
        if (user == null)
            return "redirect:/login";

        // Personal/Business Shared Data
        Wallet wallet = walletRepo.findByUserId(user.getId()).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);

        // Unified Activity Log
        List<ActivityLogDto> activityLog = getUnifiedActivityLog(user.getId());
        model.addAttribute("activityLog", activityLog);

        // Pending Requests for Current User
        model.addAttribute("pendingMoneyRequests",
                moneyRequestRepo.findByRequestedFromIdAndStatus(user.getId(), TransactionStatus.PENDING));

        // Admin Specific Data
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("allUsers", userRepo.findAll());
            model.addAttribute("pendingLoans", loanRepo.findByStatus(TransactionStatus.PENDING));
            model.addAttribute("pendingBusinessProfiles", businessProfileRepo.findByIsVerified(false));
        }

        // Notifications
        model.addAttribute("notifications", notificationRepo.findByUserIdOrderByCreatedAtDesc(user.getId()));

        return "dashboard";
    }

    private List<ActivityLogDto> getUnifiedActivityLog(Long userId) {
        List<ActivityLogDto> logs = new ArrayList<>();

        // Add Transactions
        transactionRepo.findBySenderId(userId).forEach(tx -> logs.add(ActivityLogDto.builder()
                .id(tx.getTransactionId()).type("TRANSFER").party("To: " + tx.getReceiver().getFullName())
                .amount(tx.getAmount()).date(tx.getCreatedAt()).status(tx.getStatus().name()).isOutgoing(true)
                .build()));
        transactionRepo.findByReceiverId(userId).forEach(tx -> logs.add(ActivityLogDto.builder()
                .id(tx.getTransactionId()).type("TRANSFER").party("From: " + tx.getSender().getFullName())
                .amount(tx.getAmount()).date(tx.getCreatedAt()).status(tx.getStatus().name()).isOutgoing(false)
                .build()));

        // Add Money Requests (Sender/Requester view)
        moneyRequestRepo.findByRequesterId(userId).forEach(req -> logs.add(ActivityLogDto.builder()
                .id("REQ-" + req.getId()).type("MONEY REQUEST").party("From: " + req.getRequestedFrom().getFullName())
                .amount(req.getAmount()).date(req.getCreatedAt()).status(req.getStatus().name()).isOutgoing(false)
                .build()));
        // Add Money Requests (Receiver/RequestedFrom view) - only if not pending or if
        // we want to see history
        moneyRequestRepo.findByRequestedFromId(userId).forEach(req -> {
            if (req.getStatus() != TransactionStatus.PENDING) {
                logs.add(ActivityLogDto.builder()
                        .id("REQ-" + req.getId()).type("MONEY REQUEST").party("To: " + req.getRequester().getFullName())
                        .amount(req.getAmount()).date(req.getCreatedAt()).status(req.getStatus().name())
                        .isOutgoing(true).build());
            }
        });

        // Add Loans
        loanRepo.findByUserId(userId).forEach(loan -> logs.add(ActivityLogDto.builder()
                .id("LOAN-" + loan.getId()).type("LOAN").party("RevPay Capital")
                .amount(loan.getPrincipalAmount()).date(loan.getCreatedAt()).status(loan.getStatus().name())
                .isOutgoing(false).build()));

        logs.sort(Comparator.comparing(ActivityLogDto::getDate).reversed());
        return logs;
    }

    @PostMapping("/admin/approve-business/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveBusiness(@PathVariable Long id) {
        adminService.approveBusinessProfile(id);
        return "redirect:/dashboard?success=businessApproved";
    }

    @PostMapping("/admin/approve-loan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveLoan(@PathVariable Long id) {
        adminService.approveLoan(id);
        return "redirect:/dashboard?success=loanApproved";
    }

    @PostMapping("/admin/cancel-transaction/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelTransaction(@PathVariable String id) {
        adminService.cancelTransaction(id);
        return "redirect:/dashboard?success=txCancelled";
    }

    @PostMapping("/admin/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(@RequestParam Long userId, @RequestParam String newPassword) {
        adminService.resetPassword(userId, newPassword);
        return "redirect:/dashboard?success=passwordReset";
    }

    @PostMapping("/admin/reset-pin")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPin(@RequestParam Long userId, @RequestParam String newPin) {
        adminService.resetTransactionPin(userId, newPin);
        return "redirect:/dashboard?success=pinReset";
    }

    @PostMapping("/admin/toggle-status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return "redirect:/dashboard?success=statusChanged";
    }

    @GetMapping("/admin/user-history/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUserHistory(@PathVariable Long id, Model model) {
        User user = userRepo.findById(id).orElseThrow();
        model.addAttribute("targetUser", user);
        model.addAttribute("userActivity", getUnifiedActivityLog(id));
        return "admin/user-detail :: history-fragment";
    }

    @PostMapping("/business/apply")
    public String applyBusiness(@ModelAttribute BusinessProfileRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        businessService.applyForBusinessProfile(user.getId(), dto);
        return "redirect:/dashboard?success=appliedBusiness";
    }

    @PostMapping("/loan/apply")
    public String applyLoan(@ModelAttribute LoanRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        businessService.applyForLoan(user.getId(), dto);
        return "redirect:/dashboard?success=appliedLoan";
    }

    @PostMapping("/money-request/send")
    public String sendRequest(@ModelAttribute MoneyRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        moneyRequestService.sendRequest(user.getId(), dto);
        return "redirect:/dashboard?success=requestSent";
    }

    @PostMapping("/business/invoice/create")
    @PreAuthorize("hasAnyRole('BUSINESS', 'ADMIN')")
    public String createInvoice(@ModelAttribute InvoiceDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        invoiceService.createInvoice(user.getId(), dto);
        return "redirect:/dashboard?success=invoiceCreated";
    }

    @PostMapping("/money-request/accept")
    public String acceptRequest(@RequestParam Long requestId, @RequestParam String pin) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        moneyRequestService.acceptRequest(user.getId(), requestId, pin);
        return "redirect:/dashboard?success=requestAccepted";
    }

    @PostMapping("/money-request/reject")
    public String rejectRequest(@RequestParam Long requestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmailIgnoreCase(auth.getName()).get();
        moneyRequestService.rejectRequest(user.getId(), requestId);
        return "redirect:/dashboard?success=requestRejected";
    }

    @PostMapping("/send-money")
    public String sendMoney(@ModelAttribute SendMoneyRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmail(auth.getName()).get();
        transactionService.sendMoney(user.getId(), dto);
        return "redirect:/dashboard?success=sent";
    }

    @PostMapping("/admin/add-money")
    @PreAuthorize("hasRole('ADMIN')")
    public String addMoney(@RequestParam Long userId, @RequestParam java.math.BigDecimal amount) {
        adminService.addMoneyToUser(userId, amount);
        return "redirect:/dashboard?success=moneyAdded";
    }
}
