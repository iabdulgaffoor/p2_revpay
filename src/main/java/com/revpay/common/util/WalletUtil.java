package com.revpay.common.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;

@Service
public class WalletUtil {

    @PersistenceContext
    private EntityManager entityManager;

    public String unqIdGenerator() {

        BigDecimal seqValue =
                (BigDecimal) entityManager
                        .createNativeQuery("SELECT revpay_wallet_unq_id_generator.NEXTVAL FROM dual")
                        .getSingleResult();

        String year = String.valueOf(Year.now().getValue());

        return "REV" + year + String.format("%05d", seqValue.longValue());
    }
}