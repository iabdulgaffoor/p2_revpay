package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IJpaBusinessProfileRepo extends JpaRepository<BusinessProfile, Long> {
    Optional<BusinessProfile> findByUserId(Long userId);

    List<BusinessProfile> findByIsVerified(boolean isVerified);
}
