package mymusic.spring_mymusic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import mymusic.spring_mymusic.dto.request.PermissionRequest;
import mymusic.spring_mymusic.dto.request.RoleRequest;
import mymusic.spring_mymusic.dto.response.PermissionResponse;
import mymusic.spring_mymusic.dto.response.RoleResponse;
import mymusic.spring_mymusic.entity.Permission;
import mymusic.spring_mymusic.entity.Role;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
