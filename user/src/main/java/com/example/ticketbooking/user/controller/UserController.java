package com.example.ticketbooking.user.controller;

import com.example.ticketbooking.shared.user.CreateUserDTO;
import com.example.ticketbooking.shared.user.UpdateUserDTO;
import com.example.ticketbooking.shared.user.UserDTO;
import com.example.ticketbooking.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        UserDTO createdUser = userService.registerUser(createUserDTO);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Optional<UserDTO>> getUserById(@PathVariable Long id){
        Optional<UserDTO> userDTO = userService.getUserById(id);
        if(userDTO.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDTO);

    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Optional<UserDTO>> getUserByUsername(@PathVariable String username){
        Optional<UserDTO> userDTO = userService.getUserByUsername(username);
        if(userDTO.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDTO);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDTO updateUserDTO)  {
        UserDTO updatedUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        try {
            log.debug("Attempting to delete user with id: {}", id);
            userService.deleteUser(id);
            log.debug("Successfully deleted user with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            log.warn("User with id: {} not found", id);
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            log.error("Unable to delete user with id: {} due to data integrity violation", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
