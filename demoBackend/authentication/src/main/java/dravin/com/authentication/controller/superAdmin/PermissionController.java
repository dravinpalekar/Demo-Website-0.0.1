package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.authentication.service.superAdmin.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + PERMISSION)
@Tag(name = "The permission controller will manage all types of users' permissions, like create, update, delete, view or all, and will be managed by the only super-admin.")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger( PermissionController.class );

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping(CREATE)
    @Operation(
            summary = "Create permissions.",
            description = "This API is for creating permissions."
    )
    public ResponseEntity<Map<String,String>> createPermission(@Valid @RequestBody CreatePermissionRequestModel createPermissionRequest){

        return permissionService.createPermission(createPermissionRequest);
    }

    @PutMapping(UPDATE + ID)
    @Operation(
            summary = "Update permission by id",
            description = "This API is for updating permission by id."
    )
    public ResponseEntity<Map<String,String>> updatePermissionById(@PathVariable Long id, @Valid @RequestBody CreatePermissionRequestModel createPermissionRequest){

        return permissionService.updatePermissionById(id, createPermissionRequest);
    }

    @GetMapping(GET)
    @Operation(
            summary = "Get all permissions of users",
            description = "This API is for getting all user permissions."
    )
    public ResponseEntity<Map<String,Object>> getAllPermission(){

        return permissionService.getAllPermission();
    }

    @GetMapping(GET + ID)
    @Operation(
            summary = "Get permission by id",
            description = "This API is for getting a user permission by ID."
    )
    public ResponseEntity<Map<String,Object>> getPermissionById(@PathVariable Long id){

        return permissionService.getPermissionById(id);
    }

    @DeleteMapping(DELETE + ID)
    @Operation(
            summary = "Delete permission by id",
            description = "This API is for deleting a user permission by ID."
    )
    public ResponseEntity<Map<String,String>> deletePermission(@PathVariable Long id){

        return permissionService.deletePermission(id);
    }
}
