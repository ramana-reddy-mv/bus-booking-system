package com.example.ticketbooking.user.mapper;

import com.example.ticketbooking.shared.user.CreateUserDTO;
import com.example.ticketbooking.shared.user.UpdateUserDTO;
import com.example.ticketbooking.shared.user.UserDTO;
import com.example.ticketbooking.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
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
