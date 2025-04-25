package mymusic.spring_mymusic.configuration;

import lombok.RequiredArgsConstructor;
import mymusic.spring_mymusic.constant.TokenType;
import mymusic.spring_mymusic.dto.request.TokenRequest;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.service.AuthService;
import mymusic.spring_mymusic.service.TokenService;
import org.springframework.security.oauth2.jwt.*;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final TokenService tokenService;
    private final NimbusJwtDecoder nimbusJwtDecoder;


    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // check token (signer key, blacklist, ...)
            tokenService.verifyToken(token, TokenType.ACCESS_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException(e.getMessage());
        } catch (AppException ex){
            throw new BadJwtException("Token không hợp lệ");
        }
        return nimbusJwtDecoder.decode(token);
    }
}
