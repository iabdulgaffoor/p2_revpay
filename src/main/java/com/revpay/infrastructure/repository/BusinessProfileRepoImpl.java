package com.revpay.infrastructure.repository;

import com.revpay.domain.model.BusinessProfile;
import com.revpay.domain.repository.IBusinessProfileRepo;
import com.revpay.infrastructure.persistence.IJpaBusinessProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BusinessProfileRepoImpl implements IBusinessProfileRepo {
    private final IJpaBusinessProfileRepo jpaRepo;

    @Override
    public BusinessProfile save(BusinessProfile profile) {
        return jpaRepo.save(profile);
    }

    @Override
    public Optional<BusinessProfile> findByUserId(Long userId) {
        return jpaRepo.findByUserId(userId);
    }

    @Override
    public List<BusinessProfile> findAll() {
        return jpaRepo.findAll();
    }

    @Override
    public List<BusinessProfile> findByIsVerified(boolean isVerified) {
        return jpaRepo.findByIsVerified(isVerified);
    }
}
