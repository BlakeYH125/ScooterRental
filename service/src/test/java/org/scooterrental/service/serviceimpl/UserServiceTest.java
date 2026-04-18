package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.exception.UserNotBannedException;
import org.scooterrental.model.exception.UserAlreadyAdminException;
import org.scooterrental.model.exception.UsernameAlreadyExistsException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.PasswordMismatchException;
import org.scooterrental.repository.daointerface.UserDao;
import org.scooterrental.service.dto.ChangePasswordDto;
import org.scooterrental.service.dto.UserResponseDto;
import org.scooterrental.service.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changeUserName_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        String newUsername = "abc";

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setUsername(newUsername);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.changeUsername(userId, newUsername);

        assertNotNull(actual);
        assertEquals(expected.getUsername(), actual.getUsername());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(user);
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void changeUserName_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        String newUsername = "abc";

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.changeUsername(userId, newUsername));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void changeUserName_ShouldThrowUsernameAlreadyExists_WhenUserWithSameUsernameExists() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        String newUsername = "abc";

        User user1 = new User();
        user1.setUserId(userId1);

        User user2 = new User();
        user2.setUserId(userId2);
        user2.setUsername(newUsername);

        when(userDao.findUserById(userId1)).thenReturn(user1);
        when(userDao.findUserByUsername(newUsername)).thenReturn(user2);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.changeUsername(userId1, newUsername));

        verify(userDao, times(1)).findUserById(userId1);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void changePassword_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        String newPassword = "abc";

        User user = new User();
        user.setUserId(userId);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setNewPassword(newPassword);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        userService.changePassword(userId, changePasswordDto);

        verify(userDao, times(1)).findUserById(userId);
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userDao, times(1)).update(user);
    }

    @Test
    void changePassword_ShouldThrowPasswordMismatchException_WhenUserNotFound() {
        Long userId = 1L;
        String newPassword = "abc";

        User user = new User();
        user.setUserId(userId);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setNewPassword(newPassword);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> userService.changePassword(userId, changePasswordDto));

        verify(userDao, times(1)).findUserById(userId);
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
        verify(userDao, never()).update(user);
    }

    @Test
    void changeFirstName_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        String newFirstName = "Вася";

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setFirstName(newFirstName);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.changeFirstName(userId, newFirstName);

        assertNotNull(actual);
        assertEquals(expected.getFirstName(), actual.getFirstName());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void changeFirstName_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        String newFirstName = "Вася";

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.changeFirstName(userId, newFirstName));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void changeLastName_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        String newLastName = "Петров";

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setLastName(newLastName);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.changeLastName(userId, newLastName);

        assertNotNull(actual);
        assertEquals(expected.getLastName(), actual.getLastName());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void changeLastName_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        String newLastName = "Петров";

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.changeLastName(userId, newLastName));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void changeAge_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        int newAge = 18;

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setAge(newAge);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.changeAge(userId, newAge);

        assertNotNull(actual);
        assertEquals(expected.getAge(), actual.getAge());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void changeAge_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        int newAge = 18;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.changeAge(userId, newAge));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void changeAge_ShouldThrowValueLessZeroException_WhenAgeNegative() {
        Long userId = 1L;
        int newAge = -18;

        User user = new User();
        user.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(ValueLessZeroException.class, () -> userService.changeAge(userId, newAge));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void banAccount_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBanReason("DEBT");

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.banAccount(userId, banReason);

        assertNotNull(actual);
        assertEquals(expected.getBanReason(), actual.getBanReason());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void banAccount_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.banAccount(userId, banReason));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void unbanAccount_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        BanReason banReason = BanReason.DEBT;

        User user = new User();
        user.setUserId(userId);
        user.setBanReason(banReason);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBanReason("NONE");

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.unbanAccount(userId);

        assertNotNull(actual);
        assertEquals(expected.getBanReason(), actual.getBanReason());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void unbanAccount_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.unbanAccount(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void unbanAccount_ShouldThrowUserNotBannedException_WhenUserNotBanned() {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(UserNotBannedException.class, () -> userService.unbanAccount(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void addMoney_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(500);

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBalance(amount);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.addMoney(userId, amount);

        assertNotNull(actual);
        assertEquals(amount, user.getBalance());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void addMoney_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(500);

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.addMoney(userId, amount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void addMoney_ShouldThrowValueLessZeroException_WhenAmountLessZero() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(-500);

        User user = new User();
        user.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(ValueLessZeroException.class, () -> userService.addMoney(userId, amount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void debitMoney_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(500);

        User user = new User();
        user.setUserId(userId);
        user.setBalance(amount);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.debitMoney(userId, amount);

        assertNotNull(actual);
        assertEquals(new BigDecimal(0), user.getBalance());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void debitMoney_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(500);

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.debitMoney(userId, amount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void debitMoney_ShouldThrowValueLessZeroException_WhenAmountLessZero() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(-500);

        User user = new User();
        user.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(ValueLessZeroException.class, () -> userService.debitMoney(userId, amount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void debitMoney_ShouldReturnDtoAndBanUser_WhenUserHasNotEnoughMoney() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(500);

        User user = new User();
        user.setUserId(userId);
        user.setBalance(new BigDecimal(0));

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setBanReason("DEBT");

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.debitMoney(userId, amount);

        assertNotNull(actual);
        assertEquals(expected.getBanReason(), actual.getBanReason());

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getUserById(userId);

        assertNotNull(actual);
        assertEquals(expected.getUserId(), actual.getUserId());

        verify(userDao, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnListDto_WhenAllCorrect() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        User user1 = new User();
        user1.setUserId(userId1);

        User user2 = new User();
        user2.setUserId(userId2);

        UserResponseDto userResponseDto1 = new UserResponseDto();
        userResponseDto1.setUserId(userId1);

        UserResponseDto userResponseDto2 = new UserResponseDto();
        userResponseDto2.setUserId(userId2);

        List<UserResponseDto> expected = new ArrayList<>(List.of(userResponseDto1, userResponseDto2));

        when(userDao.findUsers()).thenReturn(new ArrayList<>(List.of(user1, user2)));
        when(userMapper.toUserDto(user1)).thenReturn(userResponseDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> actual = userService.getAllUsers();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(userDao, times(1)).findUsers();
        verify(userMapper, times(2)).toUserDto(any(User.class));
    }

    @Test
    void setAdmin_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setRole(Role.ROLE_USER);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setRole(Role.ROLE_ADMIN);

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.setAdmin(userId);

        assertNotNull(actual);
        assertEquals(expected.getRole(), actual.getRole());

        verify(userDao, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void setAdmin_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.setAdmin(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void setAdmin_ShouldThrowUserAlreadyAdminException_WhenUserAlreadyAdmin() {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setRole(Role.ROLE_ADMIN);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(UserAlreadyAdminException.class, () -> userService.setAdmin(userId));

        verify(userDao, times(1)).findUserById(userId);
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void buySeasonTicket_ShouldReturnDto_WhenAllCorrect() {
        Long userId = 1L;
        int monthsCount = 3;

        User user = new User();
        user.setUserId(userId);

        UserResponseDto expected = new UserResponseDto();
        expected.setUserId(userId);
        expected.setSeasonTicketEndDate(LocalDateTime.now().plusMonths(monthsCount));

        when(userDao.findUserById(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.buySeasonTicket(userId, monthsCount);

        assertNotNull(actual);
        assertEquals(expected.getSeasonTicketEndDate().truncatedTo(ChronoUnit.MINUTES), actual.getSeasonTicketEndDate().truncatedTo(ChronoUnit.MINUTES));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, times(1)).update(any(User.class));
        verify(userMapper, times(1)).toUserDto(any(User.class));
    }

    @Test
    void buySeasonTicket_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        int monthsCount = 3;

        when(userDao.findUserById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.buySeasonTicket(userId, monthsCount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }

    @Test
    void buySeasonTicket_ShouldThrowIllegalArgumentException_WhenWrongMonthsCount() {
        Long userId = 1L;
        int monthsCount = 33;

        User user = new User();
        user.setUserId(userId);

        when(userDao.findUserById(userId)).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> userService.buySeasonTicket(userId, monthsCount));

        verify(userDao, times(1)).findUserById(userId);
        verify(userDao, never()).update(any(User.class));
        verify(userMapper, never()).toUserDto(any(User.class));
    }
}
