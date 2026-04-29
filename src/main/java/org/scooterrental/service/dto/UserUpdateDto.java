package org.scooterrental.service.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String username;
    private String firstName;
    private String lastName;
    private Integer age;
}
