package com.revpay.common.util;

import com.revpay.domain.entity.wallet.Wallet;
import com.revpay.wallet.repository.IWalletRepo;
import org.springframework.context.annotation.Configuration;

import java.time.Year;
import java.util.Optional;

@Configuration
public class WalletUtil {

    private static IWalletRepo iWalletRepo;

    public WalletUtil(IWalletRepo iWalletRepo) {
        iWalletRepo = iWalletRepo;
    }

    public static String unqIdGenerator(Wallet wallet) {
        Wallet walletWithId = iWalletRepo.save(wallet);

        String accountNumber = "REV" + Year.now().getValue()
                + String.format("%05d", walletWithId.getId());

        walletWithId.(accountNumber);
        accountRepository.save(saved);
    }

}
