package mymusic.spring_mymusic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mymusic.spring_mymusic.entity.VerificationCodeEntity;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCodeEntity, Long> {
    Optional<VerificationCodeEntity> findByEmailAndVerificationCode(String email, String verificationCode);
    void deleteByExpirationTimeBefore(long expirationTime);
}
