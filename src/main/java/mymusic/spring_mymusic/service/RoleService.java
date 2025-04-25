package mymusic.spring_mymusic.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.dto.request.RoleRequest;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.PermissionMapper;
import mymusic.spring_mymusic.mapper.RoleMapper;
import mymusic.spring_mymusic.repository.PermissionRepository;
import mymusic.spring_mymusic.repository.RoleRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final PageableService pageableService;


    public RoleResponse create(RoleRequest request){
        if(roleRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        Role role = roleMapper.toRole(request);
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()){
            List<Permission> permissions = permissionRepository.findAllByNameIn(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }else {
            role.setPermissions(new HashSet<>());
        }

        return convertRoleResponse(role);
    }


    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        roleRepository.delete(role);
    }

    public RoleResponse convertRoleResponse(Role role){
        RoleResponse response = roleMapper.toRoleResponse(roleRepository.save(role));

        Set<PermissionResponse> permissionResponse = role.getPermissions().stream()
                .map(permissionMapper::toPermissionResponse).collect(Collectors.toSet());
        response.setPermissions(permissionResponse);

        return response;
    }
    public List<RoleResponse> convertListRoleToResponse(List<Role> roleList){
        List<RoleResponse> roleResponseList = new ArrayList<>();
        roleList.forEach(role -> {
            roleResponseList.add(convertRoleResponse(role));
        });

        return roleResponseList;
    }

    public PageResponse<RoleResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Role.class);

        Page<Role> rolePage = roleRepository.findAll(pageable);

        return PageResponse.<RoleResponse>builder()
                .page(rolePage.getNumber() + 1)
                .size(rolePage.getSize())
                .totalPages(rolePage.getTotalPages())
                .totalItems(rolePage.getTotalElements())
                .items(convertListRoleToResponse(rolePage.getContent()))
                .build();
    }

    public RoleResponse fetchById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        return convertRoleResponse(role);
    }
}
