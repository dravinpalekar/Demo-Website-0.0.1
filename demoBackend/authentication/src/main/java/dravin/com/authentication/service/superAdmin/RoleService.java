package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static dravin.com.authentication.constant.Error.PERMISSION_NOT_FOUND;

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger( RoleService.class );

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }


    public ResponseEntity<?> createRole(CreateRoleRequestModel requestObject){

        Optional<RoleEntity> roleEntity = roleRepository.findByNameAndDeletedAtIsNull(requestObject.getRoleName());
        if(roleEntity.isPresent())
        {
            return ResponseEntity.badRequest().body(Map.of("message","Role already exists."));
        }
        else {
            Set<PermissionEntity> permissionEntities = new HashSet<>();
            permissionEntities.add(permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getPermissionsName()).orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND)));
            roleRepository.save(new RoleEntity(requestObject.getRoleName(), permissionEntities));
            return ResponseEntity.ok(Map.of("message", "Role created successfully."));
        }
    }

    public ResponseEntity<?> updateRoleById(Long id, CreateRoleRequestModel requestObject){

        return roleRepository.findByIdAndDeletedAtIsNull(id).map(roleEntity->{
            Optional<RoleEntity> roleEntityCheck = roleRepository.findByNameAndDeletedAtIsNull(requestObject.getRoleName());
            if(roleEntityCheck.isPresent())
            {
                return ResponseEntity.badRequest().body(Map.of("message","Role already exists."));
            }else {
                roleEntity.setName(requestObject.getRoleName());
                Set<PermissionEntity> permissionEntities = new HashSet<>();
                permissionEntities.add(permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getPermissionsName()).orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND)));
                roleEntity.setPermission(permissionEntities);
                roleRepository.save(roleEntity);
                return ResponseEntity.ok(Map.of("message", "Role updated successfully."));
            }

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Role not found")));
    }

    public ResponseEntity<?> getAllRoles() {

        return ResponseEntity.ok(Map.of("data",roleRepository.findByDeletedAtIsNull()));
    }

    public ResponseEntity<?> getRoleById(Long id){
        return ResponseEntity.ok(Map.of("data",roleRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new NullPointerException("Role not found."))));
    }

    public ResponseEntity<?> deleteRole(Long id){

        Optional<RoleEntity> roleEntity = roleRepository.findByIdAndDeletedAtIsNull(id);
        if(roleEntity.isPresent()){
            roleEntity.get().setDeletedAt(new Date());
            roleRepository.save(roleEntity.get());
            return ResponseEntity.ok(Map.of("message","Role deleted successfully."));
        }
        return ResponseEntity.badRequest().body(Map.of("message","Role not found"));
    }
}
