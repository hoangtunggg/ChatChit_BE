    package learn.spring.my_spring_project.model;

    import learn.spring.my_spring_project.dto.UserDto;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class AuthenticationResponse {
        private String accessToken;
        private UserDto userDto;
    }


