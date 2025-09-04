package learn.spring.my_spring_project.repository;

import learn.spring.my_spring_project.entity.Token;
import learn.spring.my_spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query("""
            select t from Token t
            where t.user = :user
            and (t.expired = false and t.revoked = false)
            """)

    List<Token> findAllValidTokensByUser(User user);
}
