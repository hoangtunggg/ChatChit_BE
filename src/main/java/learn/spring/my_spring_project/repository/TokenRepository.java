package learn.spring.my_spring_project.repository;

import learn.spring.my_spring_project.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {
}
