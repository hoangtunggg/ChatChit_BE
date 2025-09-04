package learn.spring.my_spring_project.service;

import jakarta.servlet.http.HttpServletResponse;
import learn.spring.my_spring_project.model.AuthenticationRequest;
import learn.spring.my_spring_project.model.AuthenticationResponse;
import learn.spring.my_spring_project.model.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response);
}
