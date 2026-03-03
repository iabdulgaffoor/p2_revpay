package com.rev.app.mapper;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setSecurityQuestion(user.getSecurityQuestion());
        dto.setSecurityAnswer(user.getSecurityAnswer());
        dto.setRole(user.getRole());
        
        dto.setBusinessName(user.getBusinessName());
        dto.setBusinessType(user.getBusinessType());
        dto.setTaxId(user.getTaxId());
        dto.setBusinessAddress(user.getBusinessAddress());
        dto.setIsBusinessVerified(user.getIsBusinessVerified());
        
        dto.setIsActive(user.getIsActive());

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setSecurityQuestion(dto.getSecurityQuestion());
        user.setSecurityAnswer(dto.getSecurityAnswer());
        user.setRole(dto.getRole());

        user.setBusinessName(dto.getBusinessName());
        user.setBusinessType(dto.getBusinessType());
        user.setTaxId(dto.getTaxId());
        user.setBusinessAddress(dto.getBusinessAddress());
        user.setIsBusinessVerified(dto.getIsBusinessVerified());
        
        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }
        
        // Note: Password & Transaction PIN aren't moved here to prevent overriding 
        // with nulls during updates from DTO to Entity.

        return user;
    }
}
