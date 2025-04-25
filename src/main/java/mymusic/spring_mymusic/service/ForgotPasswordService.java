package mymusic.spring_mymusic.service;

import com.nimbusds.jose.JOSEException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.constant.TokenType;
import mymusic.spring_mymusic.dto.request.EmailRequest;
import mymusic.spring_mymusic.dto.request.ResetPasswordRequest;
import mymusic.spring_mymusic.entity.ForgotPasswordToken;
import mymusic.spring_mymusic.repository.ForgotPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.dto.request.ChangePasswordRequest;
import mymusic.spring_mymusic.entity.User;
import mymusic.spring_mymusic.entity.VerificationCodeEntity;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.repository.UserRepository;
import mymusic.spring_mymusic.repository.VerificationCodeRepository;

import java.text.ParseException;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.reset.expiry-in-minutes}")
    private long resetTokenExpiration;

    public VerificationCodeEntity forgotPassword(EmailRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String verificationCode = generateVerificationCode();
        try {
            emailService.sendVerificationCode(user, verificationCode);

            long expirationTimeInMinutes = System.currentTimeMillis() / 60000 + (10);

            VerificationCodeEntity verificationCodeEntity = VerificationCodeEntity.builder()
                    .email(user.getEmail())
                    .verificationCode(verificationCode)
                    .expirationTime(expirationTimeInMinutes)
                    .build();

            return verificationCodeRepository.save(verificationCodeEntity);
        } catch (Exception e) {
            log.error("Lỗi gửi email: ", e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }


    public ForgotPasswordToken verifyForgotPasswordCode(String email, String verificationCode) throws JOSEException {
        VerificationCodeEntity verificationCodeEntity = verificationCodeRepository.findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        if (verificationCodeEntity.getExpirationTime() < System.currentTimeMillis() / 60000) {
            throw new AppException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String forgotPasswordToken = tokenService.generateToken(user, TokenType.RESET_PASSWORD_TOKEN);
        ForgotPasswordToken token = ForgotPasswordToken.builder()
                .email(email)
                .forgotPasswordToken(forgotPasswordToken)
                .expiryTime(LocalDateTime.now().plusMinutes(resetTokenExpiration))
                .build();

        verificationCodeRepository.delete(verificationCodeEntity);

        return forgotPasswordTokenRepository.save(token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        try {
            tokenService.verifyToken(request.getForgotPasswordToken(), TokenType.RESET_PASSWORD_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException(e.getMessage());
        } catch (AppException ex){
            throw new BadJwtException("Token không hợp lệ");
        }
        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository
                .findByForgotPasswordToken(request.getForgotPasswordToken())
                .orElseThrow( () -> new AppException(ErrorCode.FORGOT_PASSWORD_TOKEN_NOT_FOUND));

        User user = userRepository.findByEmail(forgotPasswordToken.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        forgotPasswordTokenRepository.delete(forgotPasswordToken);
    }


    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredVerificationCodes() {
        long currentTimeInMinutes = System.currentTimeMillis() / 60000; // thoi diem hien tai (phut)

        verificationCodeRepository.deleteByExpirationTimeBefore(currentTimeInMinutes);
    }

}
