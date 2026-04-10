package org.scooterrental.service.dto;

import org.scooterrental.model.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public UserResponseDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public LocalDateTime getSeasonTicketEndDate() {
        return seasonTicketEndDate;
    }

    public void setSeasonTicketEndDate(LocalDateTime seasonTicketEndDate) {
        this.seasonTicketEndDate = seasonTicketEndDate;
    }
}
