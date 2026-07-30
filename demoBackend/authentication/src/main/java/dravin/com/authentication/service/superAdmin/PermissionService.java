package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static dravin.com.authentication.constant.ConstantString.*;
import static dravin.com.authentication.constant.Error.PERMISSION_NOT_FOUND;

@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger( PermissionService.class );

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Map<String,String>> createPermission(CreatePermissionRequestModel requestObject){

        Optional<PermissionEntity> permissionEntity = permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getName());
        if(permissionEntity.isPresent())
        {
            return ResponseEntity.badRequest().body(Map.of(MESSAGE,PERMISSION_ALREADY_EXISTS));
        }
        else {
            permissionRepository.save(new PermissionEntity(requestObject.getName()));
            return ResponseEntity.ok(Map.of(MESSAGE,PERMISSION_CREATED_SUCCESSFULLY));
        }
    }

    public ResponseEntity<Map<String,String>> updatePermissionById(Long id, CreatePermissionRequestModel requestObject){
        return permissionRepository.findByIdAndDeletedAtIsNull(id)
                .map(permission -> {
                    Optional<PermissionEntity> permissionEntity = permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getName());
                    if(permissionEntity.isPresent()){
                        return ResponseEntity.badRequest().body(Map.of(MESSAGE,PERMISSION_ALREADY_EXISTS));
                    }else {
                        permission.setName(requestObject.getName());
                        permissionRepository.save(permission);
                        return ResponseEntity.ok(Map.of(MESSAGE, PERMISSION_UPDATED_SUCCESSFULLY));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, PERMISSION_NOT_FOUND)));
    }

    public ResponseEntity<Map<String,Object>> getAllPermission(){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findByDeletedAtIsNull()));
    }

    public ResponseEntity<Map<String,Object>> getPermissionById(Long id){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND))));
    }

    public ResponseEntity<Map<String,String>> deletePermission(Long id){

        Optional<PermissionEntity> permissionEntity = permissionRepository.findByIdAndDeletedAtIsNull(id);
        if(permissionEntity.isPresent())
        {
            if(roleRepository.existsByPermissionId(id)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Cannot delete permission because it is currently assigned to one or more roles."));
            }
            else
            {
                permissionEntity.get().setDeletedAt(new Date());
                permissionRepository.save(permissionEntity.get());
                return ResponseEntity.ok(Map.of(MESSAGE,PERMISSION_DELETED_SUCCESSFULLY));
            }
        }
        return ResponseEntity.badRequest().body(Map.of(MESSAGE,PERMISSION_NOT_FOUND));
    }

}
