package com.revpay.domain.repository;

import com.revpay.domain.model.BusinessProfile;
import java.util.Optional;
import java.util.List;

public interface IBusinessProfileRepo {
    BusinessProfile save(BusinessProfile profile);

    Optional<BusinessProfile> findByUserId(Long userId);

    List<BusinessProfile> findAll();

    List<BusinessProfile> findByIsVerified(boolean isVerified);
}
