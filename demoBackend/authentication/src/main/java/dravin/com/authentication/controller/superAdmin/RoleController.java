package dravin.com.authentication.controller.superAdmin;


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

    private final RoleService superAdminService;

    public RoleController(RoleService superAdminService) {
        this.superAdminService = superAdminService;
    }


    @PostMapping(CREATE)
    public ResponseEntity<?> createRole(@Valid @RequestBody CreateRoleRequestModel createRoleRequest) {

        return superAdminService.createRole(createRoleRequest);
    }

    @GetMapping(GET)
    public ResponseEntity<?> getAllRoles() {

        return superAdminService.getAllRoles();
    }
}
