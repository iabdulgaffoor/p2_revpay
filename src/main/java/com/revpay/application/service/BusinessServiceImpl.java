package com.revpay.application.service;

import com.revpay.application.dto.BusinessProfileRequestDto;
import com.revpay.application.dto.LoanRequestDto;
import com.revpay.common.exception.ResourceNotFoundException;
import com.revpay.domain.model.BusinessProfile;
import com.revpay.domain.model.Loan;
import com.revpay.domain.model.User;
import com.revpay.domain.repository.IBusinessProfileRepo;
import com.revpay.domain.repository.ILoanRepo;
import com.revpay.domain.repository.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements IBusinessService {

        private final IUserRepo userRepo;
        private final IBusinessProfileRepo businessProfileRepo;
        private final ILoanRepo loanRepo;
        private final INotificationService notificationService;

        @Override
        @Transactional
        public void applyForBusinessProfile(Long userId, BusinessProfileRequestDto request) {
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                BusinessProfile profile = BusinessProfile.builder()
                                .user(user)
                                .businessName(request.getBusinessName())
                                .businessType(request.getBusinessType())
                                .taxId(request.getTaxId())
                                .businessAddress(request.getBusinessAddress())
                                .isVerified(false)
                                .build();

                businessProfileRepo.save(profile);

                notificationService.sendNotification(user, "Business Application Submitted",
                                "Your application for " + request.getBusinessName() + " is pending verification.",
                                "SYSTEM");

                // Notify admins
                userRepo.findByRole("ADMIN").forEach(admin -> {
                        notificationService.sendNotification(admin, "New Business Verification",
                                        "Business: " + request.getBusinessName() + " (EIN: " + request.getTaxId()
                                                        + ") is pending verification.",
                                        "SYSTEM");
                });
        }

        @Override
        @Transactional
        public void applyForLoan(Long userId, LoanRequestDto request) {
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Loan loan = Loan.builder()
                                .user(user)
                                .principalAmount(request.getAmount())
                                .interestRate(new BigDecimal("0.05")) // Mock interest
                                .tenureMonths(request.getTenureMonths())
                                .emi(request.getAmount().divide(new BigDecimal(request.getTenureMonths()), 2,
                                                RoundingMode.HALF_UP))
                                .purpose(request.getPurpose())
                                .build();

                loanRepo.save(loan);

                notificationService.sendNotification(user, "Loan Application Received",
                                "Your loan application for " + request.getAmount() + " is under review.", "SYSTEM");

                // Notify admins (Dashboard, and simulated Mail/Mobile via logs in service)
                userRepo.findByRole("ADMIN").forEach(admin -> {
                        notificationService.sendNotification(admin, "New Loan Application",
                                        "User " + user.getFullName() + " applied for a loan of " + request.getAmount(),
                                        "SYSTEM");
                });
        }
}
