package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public ResponseEntity<?> getAllPermission(){

        return ResponseEntity.ok(Map.of("data",permissionRepository.findAll()));
    }

}
