package mymusic.spring_mymusic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mymusic.spring_mymusic.entity.ForgotPasswordToken;

import java.util.Optional;

@Repository
public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {

    Optional<ForgotPasswordToken> findByForgotPasswordToken(String forgotPasswordToken);

}
