package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.authentication.service.superAdmin.PermissionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + PERMISSION)
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger( PermissionController.class );

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping(CREATE)
    public ResponseEntity<?> createPermission(@Valid @RequestBody CreatePermissionRequestModel createPermissionRequest){

        return permissionService.createPermission(createPermissionRequest);
    }

    @GetMapping(GET)
    public ResponseEntity<?> getAllPermission(){

        return permissionService.getAllPermission();
    }
}
