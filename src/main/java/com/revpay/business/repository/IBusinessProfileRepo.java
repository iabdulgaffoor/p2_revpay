package com.revpay.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.business.BusinessProfile;

@Repository
public interface IBusinessProfileRepo extends JpaRepository<BusinessProfile, Long> {

}
