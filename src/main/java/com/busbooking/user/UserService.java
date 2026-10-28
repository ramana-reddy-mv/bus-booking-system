package com.busbooking.user;



import com.busbooking.user.dto.CreateUserDTO;
import com.busbooking.user.dto.UpdateUserDTO;
import com.busbooking.user.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {


    UserDTO registerUser(CreateUserDTO createUserDTO);

    Optional<UserDTO> getUserById(Long userId);

    Optional<UserDTO> getUserByUsername(String username);

    void deleteUser(Long userId);

    UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO);
}
