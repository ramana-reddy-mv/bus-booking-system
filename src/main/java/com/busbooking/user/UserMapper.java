package com.busbooking.user;

import com.busbooking.user.dto.CreateUserDTO;
import com.busbooking.user.dto.UpdateUserDTO;
import com.busbooking.user.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "userId")
    UserDTO toUserDTO(User user);

    User toUserEntity(CreateUserDTO createUserDTO);

    @Mapping(source = "userId", target = "userId")
    User toUserEntity(UpdateUserDTO updateUserDTO);

    UserDTO toUserDTO(CreateUserDTO createUserDTO);

    @Mapping(source = "userId", target = "userId")
    UserDTO toUserDTO(UpdateUserDTO updateUserDTO);
}
