package mymusic.spring_mymusic.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.constant.TokenType;
import org.springframework.security.access.prepost.PreAuthorize;
import mymusic.spring_mymusic.dto.request.*;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.entity.*;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.RoleMapper;
import mymusic.spring_mymusic.mapper.UserMapper;
import mymusic.spring_mymusic.repository.*;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final PlaylistService playlistService;
    private final RoleMapper roleMapper;
    private final InvalidatedTokenRepository invalidatedRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;


    public TokenResponse login(AuthRequest request) throws JOSEException {
        User userDB = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), userDB.getPassword());

        if(!isAuthenticated){
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        return generateAndSaveTokenResponse(userDB);
    }

    private TokenResponse generateAndSaveTokenResponse(User user) throws JOSEException {
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        String refreshToken = tokenService.generateToken(user, TokenType.REFRESH_TOKEN);

        tokenService.saveRefreshToken(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .email(user.getEmail())
                .build();
    }

    public TokenResponse register(RegisterRequest request) throws JOSEException {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = userMapper.toUserByRegister(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("USER").orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Set<Role> roles = new HashSet<>();
        if(user.getRoles() != null){
            roles.addAll(user.getRoles());
        }
        roles.add(userRole);
        user.setRoles(roles);

        UserResponse response = userMapper.toUserResponse(userRepository.save(user));

        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(roleMapper::toRoleResponse).collect(Collectors.toSet());
        response.setRoles(roleResponses);

        emailService.sendUserEmailWithRegister(user);

        return generateAndSaveTokenResponse(user);
    }



    public void logout(TokenRequest request) throws ParseException, JOSEException {
        SignedJWT signToken = tokenService.verifyToken(request.getAccessToken(), TokenType.ACCESS_TOKEN);
        String wti = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(wti)
                .expiryTime(expiryTime)
                .build();

        invalidatedRepository.save(invalidatedToken);
    }

    public UserResponse getMyInfo() {
       User user = currentUserService.getCurrentUser();

        return  convertUserResponse(user);
    }

    public StatsResponse getStatsCounts() {

        long totalUsers = userRepository.count();

        long totalAlbums = albumRepository.count();

        long totalArtists = artistRepository.count();

        long totalSongs = songRepository.count();

        long totalPlaylists = playlistRepository.count();

        return StatsResponse.builder()
                .totalUsers(totalUsers)
                .totalAlbums(totalAlbums)
                .totalArtists(totalArtists)
                .totalPlaylists(totalPlaylists)
                .totalSongs(totalSongs)
                .build();
    }

    public UserResponse convertUserResponse(User user){
        UserResponse response = userMapper.toUserResponse(user);

        if(user.getCreatedPlaylists() != null){
            List<PlaylistResponse> playlistBasicSet = playlistService.convertListPlaylistResponse(new ArrayList<>(user.getCreatedPlaylists()));
            response.setCreatedPlaylists(new HashSet<>(playlistBasicSet));
        }

        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(roleMapper::toRoleResponse).collect(Collectors.toSet());
        response.setRoles(roleResponses);
        return response;
    }

    public TokenResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // verify refresh token (db, expirationTime ...)
        SignedJWT  signedJWT = tokenService.verifyToken(request.getRefreshToken(), TokenType.REFRESH_TOKEN);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // new access token
        String accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .email(email)
                .build();
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(currentUserService.getCurrentUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
