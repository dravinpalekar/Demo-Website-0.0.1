package dravin.com.authentication.controller;


import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import dravin.com.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_AUTH)
@Tag(name = "Authentication and Authorization Controller")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger( AuthenticationController.class );

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(SIGN_IN)
    @Operation(
            summary = "Authenticate API for User Or Guest User",
            description = "Authenticate Admin and Super-AdminFeatures as well."
    )
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestModel loginRequest) {

        logger.info("Attempt user for authenticate and UserName is {}", loginRequest.getUserName());
        return authenticationService.authenticateUser(loginRequest);
    }

    @PostMapping(SIGN_UP)
    @Operation(
            summary = "Register API for User Or Guest User",
            description = "Create Admin Features as well."
    )
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestModel signUpRequest) {

        logger.info("Attempt user for register and email is {}", signUpRequest.getEmail());
        return authenticationService.registerUser(signUpRequest);
    }
}
