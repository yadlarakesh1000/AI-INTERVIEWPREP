package com.interviewprep.modules.auth.service;

import com.interviewprep.modules.auth.dto.AuthResponse;
import com.interviewprep.modules.auth.dto.LoginRequest;
import com.interviewprep.modules.auth.dto.RefreshTokenRequest;
import com.interviewprep.modules.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
