package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dravin.com.repository.constant.enumConstant.SearchFilterColumnName;
import dravin.com.repository.constant.enumConstant.SearchFilterRoleColumnName;
import dravin.com.repository.repository.specification.RoleEntitySearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static dravin.com.authentication.constant.ConstantString.*;
import static dravin.com.authentication.constant.Error.PERMISSION_NOT_FOUND;
import static dravin.com.authentication.constant.Error.ROLE_NOT_FOUND;

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public ResponseEntity<Map<String, String>> createRole(CreateRoleRequestModel requestObject) {

        Optional<RoleEntity> roleEntity = roleRepository.findByNameAndDeletedAtIsNull(requestObject.getRoleName());
        if (roleEntity.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE, ROLE_ALREADY_EXISTS));
        } else {
            Set<PermissionEntity> permissionEntities = new HashSet<>();
            permissionEntities.add(permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getPermissionsName())
                    .orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND)));
            roleRepository.save(new RoleEntity(requestObject.getRoleName(), permissionEntities));
            return ResponseEntity.ok(Map.of(MESSAGE, ROLE_CREATED_SUCCESSFULLY));
        }
    }

    public ResponseEntity<Map<String, String>> updateRoleById(Long id, CreateRoleRequestModel requestObject) {

        return roleRepository.findByIdAndDeletedAtIsNull(id).map(roleEntity -> {
            Optional<RoleEntity> roleEntityCheck = roleRepository
                    .findByNameAndDeletedAtIsNull(requestObject.getRoleName());
            if (roleEntityCheck.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(MESSAGE, ROLE_ALREADY_EXISTS));
            } else {
                roleEntity.setName(requestObject.getRoleName());
                Set<PermissionEntity> permissionEntities = new HashSet<>();
                permissionEntities
                        .add(permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getPermissionsName())
                                .orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND)));
                roleEntity.setPermission(permissionEntities);
                roleRepository.save(roleEntity);
                return ResponseEntity.ok(Map.of(MESSAGE, ROLE_UPDATED_SUCCESSFULLY));
            }

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(MESSAGE, ROLE_NOT_FOUND)));
    }

    public ResponseEntity<Map<String, Object>> getAllRoles(Pageable pageable, SearchFilterRoleColumnName ColumnName,
            String searchItem) {

        Specification<RoleEntity> specification = RoleEntitySearchSpecification.notDeleted()
                .and(RoleEntitySearchSpecification.search(ColumnName, searchItem));
        Page<RoleEntity> roleEntities = roleRepository.findAll(specification, pageable);
        return ResponseEntity.ok(Map.of(
                "data", roleEntities.getContent(),
                "pageSize", roleEntities.getSize(),
                "getTotalElements", roleEntities.getTotalElements()));
    }

    public ResponseEntity<Map<String, Object>> getRoleById(Long id) {
        return ResponseEntity.ok(Map.of("data", roleRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NullPointerException(ROLE_NOT_FOUND))));
    }

    public ResponseEntity<Map<String, String>> deleteRole(Long id) {

        Optional<RoleEntity> roleEntity = roleRepository.findByIdAndDeletedAtIsNull(id);
        if (roleEntity.isPresent()) {
            roleEntity.get().setDeletedAt(new Date());
            roleRepository.save(roleEntity.get());
            return ResponseEntity.ok(Map.of(MESSAGE, ROLE_DELETED_SUCCESSFULLY));
        }
        return ResponseEntity.badRequest().body(Map.of(MESSAGE, ROLE_NOT_FOUND));
    }
}
