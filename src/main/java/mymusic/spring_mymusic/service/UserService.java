package mymusic.spring_mymusic.service;

import jakarta.validation.constraints.Min;
import mymusic.spring_mymusic.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import mymusic.spring_mymusic.dto.request.UserRequest;
import mymusic.spring_mymusic.dto.request.UserUpdateRequest;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.entity.Playlist;
import mymusic.spring_mymusic.entity.Role;
import mymusic.spring_mymusic.entity.User;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.PlaylistMapper;
import mymusic.spring_mymusic.mapper.RoleMapper;
import mymusic.spring_mymusic.mapper.UserMapper;
import mymusic.spring_mymusic.repository.PlaylistRepository;
import mymusic.spring_mymusic.repository.RoleRepository;
import mymusic.spring_mymusic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PlaylistRepository playlistRepository;
    private final PageableService pageableService;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PlaylistService playlistService;


    public UserResponse create(UserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // playlist
        if(request.getCreatedPlaylists() != null && !request.getCreatedPlaylists().isEmpty()){
            List<Playlist> playlistList = playlistRepository.findAllByIdIn(new ArrayList<>(request.getCreatedPlaylists()));

            for(Playlist playlist : playlistList){
                playlist.setListener(playlist.getListener() + 1);
                playlist.setCreator(user.getName());
            }
           user.setCreatedPlaylists(new HashSet<>(playlistList));
        }
        else{
            user.setCreatedPlaylists(new HashSet<>());
        }

        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            List<Role> roleList = roleRepository.findAllByIdIn(request.getRoles());
            user.setRoles(new HashSet<>(roleList));
        }
        else{
            Role userRole = roleRepository.findByName("USER").orElseThrow(
                    () -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        }

        return convertUserResponse(userRepository.save(user));
    }


    public UserResponse fetchById(long id){
        User userDB = userRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return convertUserResponse(userDB);
    }

    public User convertUpdateUser(User userDB, UserUpdateRequest request){
        if (request == null) {
            return userDB;
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            userDB.setName(request.getName());
        }
        if (request.getDob() != null) {
            userDB.setDob(request.getDob());
        }
        return userDB;
    }

    public UserResponse update(long id, UserUpdateRequest request) {
        User userDB = userRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User user = convertUpdateUser(userDB, request);
        // playlist
        if(request.getCreatedPlaylists() != null){
            List<Playlist> playlistList = playlistRepository.findAllByIdIn(new ArrayList<>(request.getCreatedPlaylists()));

            for(Playlist playlist : playlistList){
                playlist.setListener(playlist.getListener() + 1);
                playlist.setCreator(user.getName());
            }
            Set<Playlist> playlistSet = userDB.getCreatedPlaylists();
            playlistSet.addAll(playlistList);
            user.setCreatedPlaylists(playlistSet);
        }
        else{
            user.setCreatedPlaylists(new HashSet<>());
        }

        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            List<Role> roleList = roleRepository.findAllByIdIn(request.getRoles());
            user.setRoles(new HashSet<>(roleList));
        }

        return convertUserResponse(userRepository.save(user));
    }

    public void deleteUser(long id){
        User userDB = userRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.delete(userDB);
    }

    public PlaylistResponse createSavedPlaylists(long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXISTED));

        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Long> idList = user.getSavedPlaylistId();
        idList.add(playlist.getId());
        user.setSavedPlaylistId(idList);

        userRepository.save(user);

        return playlistService.convertPlaylistResponse(playlist);
    }

    public List<PlaylistResponse> fetchSavedPlaylists(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Playlist> savedList = playlistRepository.findAllByIdIn(user.getSavedPlaylistId());

        return playlistService.convertListPlaylistResponse(savedList);
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

    public List<UserResponse> convertListUserResponse(List<User> userList){
        List<UserResponse> userResponseList = new ArrayList<>();
        for(User user : userList){
            UserResponse response = convertUserResponse(user);
            userResponseList.add(response);
        }
        return userResponseList;
    }
    public void removeSavedPlaylist(long playlistId) {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getSavedPlaylistId().contains(playlistId)) {
            throw new AppException(ErrorCode.PLAYLIST_NOT_IN_USER);
        }

        user.getSavedPlaylistId().remove(playlistId);
        userRepository.save(user);
    }

    public PageResponse<UserResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, User.class);

        Page<User> userPage = userRepository.findAll(pageable);

        return PageResponse.<UserResponse>builder()
                .page(userPage.getNumber() + 1)
                .size(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .totalItems(userPage.getTotalElements())
                .items(convertListUserResponse(userPage.getContent()))
                .build();
    }
}
