package mymusic.spring_mymusic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import mymusic.spring_mymusic.dto.request.RoleRequest;
import mymusic.spring_mymusic.dto.response.RoleResponse;
import mymusic.spring_mymusic.entity.Role;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    @Mapping(target = "permissions", ignore = true)

    RoleResponse toRoleResponse(Role role);
}
