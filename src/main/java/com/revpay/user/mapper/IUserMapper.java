package com.revpay.user.mapper;

import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.CreateUserRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    User toEntity(CreateUserRequestDto userDto);

}
