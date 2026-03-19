package dravin.com.authentication.controller;


import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import dravin.com.authentication.service.AuthenticationService;
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
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger( AuthenticationController.class );

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(SIGN_IN)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestModel loginRequest) {

        return authenticationService.authenticateUser(loginRequest);
    }

    @PostMapping(SIGN_UP)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestModel signUpRequest) {

        return authenticationService.registerUser(signUpRequest);
    }
}
