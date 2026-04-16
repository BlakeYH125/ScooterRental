package org.scooterrental.service.serviceimpl.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "VGhpcyBpcyBhIHNlY3JldCBrZXkgZm9yIHRlc3RpbmcgSldUIGFsZ29yaXRobXM=";
    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnString_WhenAllCorrect() {
        String username = "abc";

        String token = jwtService.generateToken(username);

        assertNotNull(token);
    }

    @Test
    void extractUsername_ShouldReturnUsername_WhenAllCorrect() {
        String username = "username";

        String token = jwtService.generateToken(username);

        String actual = jwtService.extractUsername(token);

        assertNotNull(actual);
        assertEquals(username, actual);
    }

    @Test
    void extractExpiration_ShouldReturnExpiration_WhenAllCorrect() {
        String username = "username";
        Date expectedDate = new Date(System.currentTimeMillis() + EXPIRATION);

        String token = jwtService.generateToken(username);

        Date actualDate = jwtService.extractExpiration(token);

        assertNotNull(actualDate);

        assertEquals(Date.from(expectedDate.toInstant().truncatedTo(ChronoUnit.MINUTES)),
                Date.from(actualDate.toInstant().truncatedTo(ChronoUnit.MINUTES)));
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_WhenTokenNotExpired() {
        String username = "username";

        String token = jwtService.generateToken(username);

        boolean result = jwtService.isTokenExpired(token);

        assertFalse(result);
    }

    @Test
    void isTokenExpired_ShouldThrowExpiredJwtException_WhenTokenExpired() {
        String username = "username";
        ReflectionTestUtils.setField(jwtService, "expiration", -1000000L);

        String token = jwtService.generateToken(username);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenValidAndNotExpired() {
        String username = "username";

        String token = jwtService.generateToken(username);

        boolean result = jwtService.isTokenValid(token, username);

        assertTrue(result);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenNotValidAndNotExpired() {
        String username = "username";

        String token = jwtService.generateToken(username);

        boolean result = jwtService.isTokenValid(token, "otherUser");

        assertFalse(result);
    }
}
