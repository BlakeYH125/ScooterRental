package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.scooterrental.model.entity.User;
import org.scooterrental.service.dto.UserCreateDto;
import org.scooterrental.service.dto.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toUserDto(User user);
    User toUserEntity(UserCreateDto userCreateDto);
}
