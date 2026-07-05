package com.interviewprep.modules.auth.serviceimpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewprep.modules.auth.dto.AuthResponse;
import com.interviewprep.modules.auth.dto.LoginRequest;
import com.interviewprep.modules.auth.dto.RefreshTokenRequest;
import com.interviewprep.modules.auth.dto.RegisterRequest;
import com.interviewprep.modules.auth.entity.RefreshToken;
import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.exception.AuthException;
import com.interviewprep.modules.auth.repository.RefreshTokenRepository;
import com.interviewprep.modules.auth.repository.UserRepository;
import com.interviewprep.modules.auth.service.AuthService;
import com.interviewprep.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            log.warn("Registration attempt with existing email: {}", request.getEmail());
            throw new DataIntegrityViolationException("Email is already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .build();
        User savedUser = userRepository.save(user);
        log.info("Registered new user id={} email={}", savedUser.getId(), savedUser.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getEmail(), savedUser.getId());
        RefreshToken refreshToken = createRefreshToken(savedUser);

        return buildAuthResponse(savedUser, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new AuthException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        // A user keeps a single active refresh token — remove any previous ones.
        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());
        RefreshToken refreshToken = createRefreshToken(user);
        log.info("User logged in id={} email={}", user.getId(), user.getEmail());

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (Boolean.TRUE.equals(refreshToken.getIsRevoked())) {
            throw new AuthException("Refresh token has been revoked");
        }
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthException("Refresh token has expired. Please log in again.");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());

        // Rotate the refresh token for security.
        refreshToken.setToken(jwtTokenProvider.generateRefreshToken());
        refreshToken.setExpiryDate(refreshTokenExpiry());
        RefreshToken rotated = refreshTokenRepository.save(refreshToken);
        log.info("Refreshed tokens for user id={} email={}", user.getId(), user.getEmail());

        return buildAuthResponse(user, accessToken, rotated.getToken());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtTokenProvider.generateRefreshToken())
                .expiryDate(refreshTokenExpiry())
                .isRevoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private LocalDateTime refreshTokenExpiry() {
        return LocalDateTime.now().plus(jwtTokenProvider.getRefreshTokenExpirationMs(), ChronoUnit.MILLIS);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
