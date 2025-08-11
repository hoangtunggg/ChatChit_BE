package learn.spring.my_spring_project.service.impl;


import learn.spring.my_spring_project.dto.UserDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        String jwtToken = jwtService.generateToken(createdUser);
        Token token = Token.builder()
                .userId(createdUser.getUserId())
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
        return AuthenticationResponse.builder()
                .userDto(UserMapper.mapToUserDto(createdUser))
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
        UserDto userDto = UserMapper.mapToUserDto(user);
        return AuthenticationResponse.builder()
                .userDto(userDto)
                .token(jwtToken)
                .build();
    }
}
