package learn.spring.my_spring_project.mapper;

import learn.spring.my_spring_project.dto.UserDto;
import learn.spring.my_spring_project.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user){
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
