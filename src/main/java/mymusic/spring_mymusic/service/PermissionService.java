package mymusic.spring_mymusic.service;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import mymusic.spring_mymusic.dto.response.ArtistResponse;
import mymusic.spring_mymusic.dto.response.GenreResponse;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.dto.request.PermissionRequest;
import mymusic.spring_mymusic.dto.response.PermissionResponse;
import mymusic.spring_mymusic.entity.Permission;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.PermissionMapper;
import mymusic.spring_mymusic.repository.PermissionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final PageableService pageableService;


    public PermissionResponse create(PermissionRequest request){
        if(permissionRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }



    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        permissionRepository.delete(permission);
    }

    public List<PermissionResponse> convertListPermissionToResponse(List<Permission> permissionList){
        List<PermissionResponse> permissionResponseList = new ArrayList<>();
        permissionList.forEach(permission  -> {
            permissionResponseList.add(permissionMapper.toPermissionResponse(permission));
        });

        return permissionResponseList;
    }


    public PageResponse<PermissionResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Permission.class);

        Page<Permission> permissionPage = permissionRepository.findAll(pageable);

        return PageResponse.<PermissionResponse>builder()
                .page(permissionPage.getNumber() + 1)
                .size(permissionPage.getSize())
                .totalPages(permissionPage.getTotalPages())
                .totalItems(permissionPage.getTotalElements())
                .items(convertListPermissionToResponse(permissionPage.getContent()))
                .build();
    }

    public PermissionResponse fetchById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        return permissionMapper.toPermissionResponse(permission);
    }
}
