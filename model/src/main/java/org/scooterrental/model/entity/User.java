package org.scooterrental.model.entity;

import jakarta.persistence.*;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "ban_reason", nullable = false)
    private BanReason banReason = BanReason.NONE;

    @Column(name = "season_ticket_end_date")
    private LocalDateTime seasonTicketEndDate = null;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, int age, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public BanReason getBanReason() {
        return banReason;
    }

    public void setBanReason(BanReason banReason) {
        this.banReason = banReason;
    }

    public LocalDateTime getSeasonTicketEndDate() {
        return seasonTicketEndDate;
    }

    public void setSeasonTicketEndDate(LocalDateTime seasonTicketEndDate) {
        this.seasonTicketEndDate = seasonTicketEndDate;
    }
}
