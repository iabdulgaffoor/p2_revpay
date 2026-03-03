package com.rev.app.repository;

import com.rev.app.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWalletRepository extends JpaRepository<Wallet, Long> {

    @Override
    <S extends Wallet> S save(S entity);

    @Override
    Optional<Wallet> findById(Long id);

    @Override
    List<Wallet> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(Wallet entity);

    Optional<Wallet> findByUserId(Long userId);
}
