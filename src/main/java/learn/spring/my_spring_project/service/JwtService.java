package learn.spring.my_spring_project.service;

import learn.spring.my_spring_project.entity.User;

public interface JwtService {
    String extractUsername(String token);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
}
