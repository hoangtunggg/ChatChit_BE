package learn.spring.my_spring_project.service;

import learn.spring.my_spring_project.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getUsers();

    User getUserById(Long userId);

    User updateUser(Long userId, User user);
}
