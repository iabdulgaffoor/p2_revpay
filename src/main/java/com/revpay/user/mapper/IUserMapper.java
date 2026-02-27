package com.revpay.user.mapper;

import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.dto.UserProfileResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    default User toEntity(CreateUserRequestDto dto) {
        User user = new User(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone()
        );
        return user;
    }

    //User toEntity(CreateUserRequestDto userDto);

    UserProfileResponseDto toResponseDto(User user);

}
