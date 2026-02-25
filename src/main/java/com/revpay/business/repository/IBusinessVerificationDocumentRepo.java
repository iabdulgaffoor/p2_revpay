package com.revpay.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.business.BusinessVerificationDocument;

@Repository
public interface IBusinessVerificationDocumentRepo extends JpaRepository<BusinessVerificationDocument, Long> {

}
