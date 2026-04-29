package org.scooterrental.service.dto;

import lombok.Data;
import org.scooterrental.model.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long userId;

    private String username;

    private String firstName;

    private String lastName;

    private int age;

    private BigDecimal balance;

    private Role role;

    private String banReason;

    private LocalDateTime seasonTicketEndDate;
}
