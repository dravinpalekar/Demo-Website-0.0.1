package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.ActivateRequestModel;
import dravin.com.authentication.service.superAdmin.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + USER)
@Tag(name = "The user controller for managing users, like deleting and activating/deactivating, will be managed by the only super-admin.")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger( UserController.class );

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(GET)
    @Operation(
            summary = "Get all user lists",
            description = "This API is for getting all users of active or deactivate user."
    )
    public ResponseEntity<?> getAllUser(){

        return userService.getAllUser();
    }

    @DeleteMapping(DELETE + ID)
    @Operation(
            summary = "Delete user by Id",
            description = "This API is for deleting any user by ID."
    )
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){

        return userService.deleteUserById(id);
    }

    @PostMapping(ACTIVE_DEACTIVATE)
    @Operation(
            summary = "Enable or disable user by Id",
            description = "This API is for enabling or disabling any user by ID."
    )
    public ResponseEntity<?> activeAndDeactivateUserByID(@Valid @RequestBody ActivateRequestModel requestModel){
        return userService.activeAndDeactivateUserByID(requestModel);
    }

}
