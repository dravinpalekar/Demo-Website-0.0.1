package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.authentication.service.superAdmin.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import dravin.com.repository.constant.enumConstant.SearchFilterColumnName;
import dravin.com.repository.constant.enumConstant.SearchFilterRoleColumnName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + ROLE)
@Tag(name = "The role controller will manage all types of users' roles, like guest, user, admin or super-admin, and will be managed by the only super-admin.")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(CREATE)
    @Operation(summary = "Create Roles", description = "This API is for creating roles.")
    public ResponseEntity<Map<String, String>> createRole(
            @Valid @RequestBody CreateRoleRequestModel createRoleRequest) {

        return roleService.createRole(createRoleRequest);
    }

    @PutMapping(UPDATE + ID)
    @Operation(summary = "Update role by id", description = "This API is for updating roles by id.")
    public ResponseEntity<Map<String, String>> updateRoleById(@PathVariable Long id,
            @Valid @RequestBody CreateRoleRequestModel createRoleRequestModel) {

        return roleService.updateRoleById(id, createRoleRequestModel);
    }

    @GetMapping(GET)
    @Operation(summary = "Get all roles of users", description = "This API is for getting all user roles.")
    public ResponseEntity<Map<String, Object>> getAllRoles(
            @RequestParam(required = false) String searchItem,
            @RequestParam(required = false) SearchFilterRoleColumnName ColumnName,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return roleService.getAllRoles(pageable, ColumnName, searchItem);
    }

    @GetMapping(GET + ID)
    @Operation(summary = "Get role by id", description = "This API is for getting a user role by ID.")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long id) {

        return roleService.getRoleById(id);
    }

    @DeleteMapping(DELETE + ID)
    @Operation(summary = "Delete role by id", description = "This API is for deleting a user role by ID.")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {

        return roleService.deleteRole(id);
    }
}
