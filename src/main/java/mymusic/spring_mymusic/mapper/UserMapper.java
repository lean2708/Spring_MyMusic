package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.request.RegisterRequest;
import mymusic.spring_mymusic.dto.request.UserRequest;
import mymusic.spring_mymusic.dto.request.UserUpdateRequest;
import mymusic.spring_mymusic.dto.response.UserResponse;
import mymusic.spring_mymusic.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdPlaylists", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest request);
    @Mapping(target = "createdPlaylists", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toUserByRegister(RegisterRequest request);
}
