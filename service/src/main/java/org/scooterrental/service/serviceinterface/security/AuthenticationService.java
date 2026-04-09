package org.scooterrental.service.serviceinterface.security;

import org.scooterrental.service.dto.AuthenticationResponseDto;
import org.scooterrental.service.dto.LoginRequest;
import org.scooterrental.service.dto.UserCreateDto;

public interface AuthenticationService {
    AuthenticationResponseDto register(UserCreateDto userCreateDto);
    AuthenticationResponseDto login(LoginRequest loginRequest);
}
