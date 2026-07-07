package dravin.com.authentication.controller;


import dravin.com.authentication.requestmodel.LoginRequestModel;
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

import static dravin.com.authentication.constant.RoutesFile.API_AUTH;
import static dravin.com.authentication.constant.RoutesFile.SIGN_IN;

@RestController
@RequestMapping(API_AUTH)
@Tag(name = "Authentication and Authorization Controller")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger( AuthenticationController.class );

    @PostMapping(SIGN_IN)
    @Operation(
            summary = "Authenticate API for User Or Guest User",
            description = "Authenticate Admin and Super-AdminFeatures as well."
    )
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestModel loginRequest) {

        logger.info("Attempt user for authenticate and UserName is {}", loginRequest.getUserName());
        return authenticationService.authenticateUser(loginRequest);
    }
}
