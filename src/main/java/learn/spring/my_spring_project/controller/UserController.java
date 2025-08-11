package learn.spring.my_spring_project.controller;

import learn.spring.my_spring_project.entity.User;
import learn.spring.my_spring_project.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // = controller + response body: tra ve JSON/XML
@RequestMapping("/api/users") // prefix
@RequiredArgsConstructor

public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<User> createUser (@RequestBody User user){
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers (){
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser (@PathVariable Long userId){
        User searchedUser = userService.getUserById(userId);
        return ResponseEntity.ok(searchedUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(updatedUser);
    }
}
