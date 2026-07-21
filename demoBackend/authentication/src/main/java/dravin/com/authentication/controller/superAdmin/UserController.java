package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.ActivateRequestModel;
import dravin.com.authentication.service.superAdmin.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + USER)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger( UserController.class );

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(GET)
    public ResponseEntity<?> getAllUser(){

        return userService.getAllUser();
    }

    @DeleteMapping(DELETE + ID)
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){

        return userService.deleteUserById(id);
    }

    @PostMapping(ACTIVE_DEACTIVATE)
    public ResponseEntity<?> activeAndDeactivateUserByID(@Valid @RequestBody ActivateRequestModel requestModel){
        return userService.activeAndDeactivateUserByID(requestModel);
    }

}
