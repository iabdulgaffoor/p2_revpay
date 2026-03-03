package com.rev.app.mapper;

import com.rev.app.dto.WalletDTO;
import com.rev.app.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletDTO toDTO(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setBalance(wallet.getBalance());
        
        if (wallet.getUser() != null) {
            dto.setUserId(wallet.getUser().getId());
        }

        return dto;
    }

    public Wallet toEntity(WalletDTO dto) {
        if (dto == null) {
            return null;
        }

        Wallet wallet = new Wallet();
        wallet.setId(dto.getId());
        wallet.setBalance(dto.getBalance());
        
        // Note: The User object must be fetched and set by the Service layer
        
        return wallet;
    }
}
