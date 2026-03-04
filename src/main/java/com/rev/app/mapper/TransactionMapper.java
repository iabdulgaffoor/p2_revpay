package com.rev.app.mapper;

import com.rev.app.dto.TransactionDTO;
import com.rev.app.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setNote(transaction.getNote());

        if (transaction.getSender() != null) {
            dto.setSenderId(transaction.getSender().getId());
            dto.setSenderName(transaction.getSender().getFullName());
        }

        if (transaction.getRecipient() != null) {
            dto.setRecipientId(transaction.getRecipient().getId());
            dto.setRecipientName(transaction.getRecipient().getFullName());
        }

        return dto;
    }

    public Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setStatus(dto.getStatus());
        transaction.setTimestamp(dto.getTimestamp());
        transaction.setNote(dto.getNote());

        // Note: User objects (Sender/Recipient) must be explicitly set by the Service
        // layer

        return transaction;
    }

    public com.rev.app.entity.Wallet toWalletEntity(com.rev.app.dto.WalletDTO dto) {
        if (dto == null) {
            return null;
        }
        com.rev.app.entity.Wallet wallet = new com.rev.app.entity.Wallet();
        wallet.setId(dto.getId());
        wallet.setBalance(dto.getBalance());
        return wallet;
    }
}
