package com.busbooking.user;

import com.busbooking.user.dto.CreateUserDTO;
import com.busbooking.user.dto.UpdateUserDTO;
import com.busbooking.user.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO registerUser(CreateUserDTO createUserDTO){
        if (createUserDTO.getPassword() == null || createUserDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        User user = userMapper.toUserEntity(createUserDTO);
        //user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set validated roles on the User entity
        user.setRole(Role.USER);

        // Save the User entity to the repository
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> getUserById(Long userId){
        return userRepository.findById(userId).map(userMapper::toUserDTO);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username){
        return Optional.ofNullable(userRepository.findByUsername(username)).map(userMapper::toUserDTO);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Delete the user
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) throws UserNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("user not found with id "+userId));

        Optional.ofNullable(updateUserDTO.getEmail()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(updateUserDTO.getFirstName()).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(updateUserDTO.getLastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(updateUserDTO.getUsername()).ifPresent(existingUser::setUsername);
        Optional.ofNullable(updateUserDTO.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);

        User updateUser = userRepository.save(existingUser);
        return userMapper.toUserDTO(updateUser);
    }
}
