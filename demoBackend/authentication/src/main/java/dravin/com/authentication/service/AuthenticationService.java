package dravin.com.authentication.service;


import dravin.com.authentication.requestmodel.LoginRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger( AuthenticationService.class );

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    public ResponseEntity<?> authenticateUser(LoginRequestModel requestObject){

        Authentication authenticationObject = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestObject.getUserName(), requestObject.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticationObject);

        return ResponseEntity.ok(Map.of("token",jwtUtils.generateJwtToken(authenticationObject)));
    }
}
