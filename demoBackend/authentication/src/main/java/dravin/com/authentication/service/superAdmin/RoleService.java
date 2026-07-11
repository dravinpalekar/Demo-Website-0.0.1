package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        Set<PermissionEntity> permissionEntities = new HashSet<>();
        permissionEntities.add(permissionRepository.findByName(requestObject.getPermissionsName()).orElseThrow(() -> new NullPointerException(PERMISSION_NOT_FOUND)));

        roleRepository.save(new RoleEntity(requestObject.getRoleName(),permissionEntities));

        return ResponseEntity.ok(Map.of("message","Role created successfully."));
    }

    public ResponseEntity<?> getAllRoles() {

        return ResponseEntity.ok(Map.of("data",roleRepository.findAll()));
    }
}
