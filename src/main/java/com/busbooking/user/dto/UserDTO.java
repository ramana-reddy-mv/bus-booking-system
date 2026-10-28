package com.busbooking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long userId;
    private String username;
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
