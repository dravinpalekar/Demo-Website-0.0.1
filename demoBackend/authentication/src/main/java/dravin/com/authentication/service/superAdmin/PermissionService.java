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
            return ResponseEntity.badRequest().body(Map.of("message","Permission already exists."));
        }
        else {
            permissionRepository.save(new PermissionEntity(requestObject.getName()));
            return ResponseEntity.ok(Map.of("message","Permission created successfully."));
        }
    }

    public ResponseEntity<Map<String,String>> updatePermissionById(Long id, CreatePermissionRequestModel requestObject){
        return permissionRepository.findByIdAndDeletedAtIsNull(id)
                .map(permission -> {
                    Optional<PermissionEntity> permissionEntity = permissionRepository.findByNameAndDeletedAtIsNull(requestObject.getName());
                    if(permissionEntity.isPresent()){
                        return ResponseEntity.badRequest().body(Map.of("message","Permission already exists."));
                    }else {
                        permission.setName(requestObject.getName());
                        permissionRepository.save(permission);
                        return ResponseEntity.ok(Map.of("message", "Permission updated successfully."));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Permission not found")));
    }

    public ResponseEntity<Map<String,Object>> getAllPermission(){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findByDeletedAtIsNull()));
    }

    public ResponseEntity<Map<String,Object>> getPermissionById(Long id){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new NullPointerException("Permission not found."))));
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
                return ResponseEntity.ok(Map.of("message","Permission deleted successfully."));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message","Permission not found"));
    }

}
