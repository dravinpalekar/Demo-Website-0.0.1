package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger( PermissionService.class );

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public ResponseEntity<?> createPermission(CreatePermissionRequestModel requestObject){

        permissionRepository.save(new PermissionEntity(requestObject.getName()));

        return ResponseEntity.ok(Map.of("message","Permission created successfully."));
    }

    public ResponseEntity<?> updatePermissionById(Long id, CreatePermissionRequestModel requestObject){
        return permissionRepository.findById(id)
                .map(permission -> {
                    permission.setName(requestObject.getName());
                    permissionRepository.save(permission);
                    return ResponseEntity.ok(Map.of("message", "Permission updated successfully."));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Permission not found")));
    }

    public ResponseEntity<?> getAllPermission(){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findAll()));
    }

    public ResponseEntity<?> getPermissionById(Long id){
        return ResponseEntity.ok(Map.of("data",permissionRepository.findById(id).orElseThrow(() -> new NullPointerException("Permission not found."))));
    }

    public ResponseEntity<?> deletePermission(Long id){

        if(permissionRepository.existsById(id))
        {
            try{
                permissionRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message","Permission deleted successfully."));
            }catch (DataIntegrityViolationException e) {
                // This catches the foreign key constraint failure
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Cannot delete permission because it is currently assigned to one or more roles."));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message","Permission not found"));
    }

}
