package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.authentication.service.superAdmin.RoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + ROLE)
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger( RoleController.class );

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @PostMapping(CREATE)
    public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequestModel createRoleRequest) {

        return roleService.createRole(createRoleRequest);
    }

    @PutMapping(UPDATE + ID)
    public ResponseEntity<?> updateRoleById(@PathVariable Long id, @Valid @RequestBody CreateRoleRequestModel createRoleRequestModel){

        return roleService.updateRoleById(id, createRoleRequestModel);
    }


    @GetMapping(GET)
    public ResponseEntity<?> getAllRoles() {

        return roleService.getAllRoles();
    }

    @GetMapping(GET + ID)
    public ResponseEntity<?> getRoleById(@PathVariable Long id){

        return roleService.getRoleById(id);
    }

    @DeleteMapping(DELETE + ID)
    public ResponseEntity<?> deleteRole(@PathVariable Long id){

        return roleService.deleteRole(id);
    }
}
