package learn.spring.my_spring_project.dto;

import learn.spring.my_spring_project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Date dateOfBirth;
    private String address;
    private String phoneNumber;
    private Role role;
}
