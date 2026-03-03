package com.rev.app.repository;

import com.rev.app.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    @Override
    <S extends LoanApplication> S save(S entity);

    @Override
    Optional<LoanApplication> findById(Long id);

    @Override
    List<LoanApplication> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(LoanApplication entity);

    List<LoanApplication> findByBusinessUserId(Long businessUserId);
}
