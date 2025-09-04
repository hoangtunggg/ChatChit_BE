package learn.spring.my_spring_project.service.impl;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learn.spring.my_spring_project.entity.Token;
import learn.spring.my_spring_project.entity.User;
import learn.spring.my_spring_project.mapper.UserMapper;
import learn.spring.my_spring_project.model.AuthenticationRequest;
import learn.spring.my_spring_project.model.AuthenticationResponse;
import learn.spring.my_spring_project.model.RegisterRequest;
import learn.spring.my_spring_project.model.Role;
import learn.spring.my_spring_project.repository.TokenRepository;
import learn.spring.my_spring_project.repository.UserRepository;
import learn.spring.my_spring_project.service.AuthenticationService;
import learn.spring.my_spring_project.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtServiceImpl jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setRole(Role.ADMIN);

        User createdUser = userRepository.save(newUser);

        revokeAllUserTokens(createdUser);

        String accessToken = jwtService.generateAccessToken(createdUser);
        String refreshToken = jwtService.generateRefreshToken(createdUser);

        saveUserToken(createdUser, refreshToken);

        return AuthenticationResponse.builder()
                .userDto(UserMapper.mapToUserDto(createdUser))
                .accessToken(accessToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        revokeAllUserTokens(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, refreshToken);

        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        return AuthenticationResponse.builder()
                .userDto(UserMapper.mapToUserDto(user))
                .accessToken(accessToken)
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // lấy refresh token từ cookie
        String refreshToken = CookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = storedToken.getUser();

        if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // revoke token cũ
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);

        // phát hành token mới
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, newRefreshToken);

        // set refresh token mới vào cookie
        CookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        return AuthenticationResponse.builder()
                .userDto(UserMapper.mapToUserDto(user))
                .accessToken(newAccessToken)
                .build();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            tokenRepository.findByToken(refreshToken).ifPresent(token -> {
                token.setExpired(true);
                token.setRevoked(true);
                tokenRepository.save(token);
            });
        }
        // Xóa cookie
        CookieUtil.clearRefreshTokenCookie(response);
    }



    private void saveUserToken(User user, String refreshToken) {
        Token token = Token.builder()
                .user(user)
                .token(refreshToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user);
        if (validTokens.isEmpty()) return;
        validTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }
}
