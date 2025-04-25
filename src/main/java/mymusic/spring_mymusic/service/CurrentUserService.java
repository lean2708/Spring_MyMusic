package mymusic.spring_mymusic.service;

import mymusic.spring_mymusic.entity.User;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    // info tu access token
    public String getCurrentUsername(){
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName(); // email
    }

    public User getCurrentUser(){
        return  userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}
