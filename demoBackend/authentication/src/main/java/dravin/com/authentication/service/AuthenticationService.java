package dravin.com.authentication.service;


import dravin.com.authentication.configuration.jwt.JwtUtils;
import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.RoleRepository;
import dravin.com.repository.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dravin.com.authentication.constant.Error.ROLE_NOT_FOUND;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger( AuthenticationService.class );

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Map<String,String>> authenticateUser(LoginRequestModel requestObject){

        Authentication authenticationObject = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestObject.getUserName(), requestObject.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticationObject);

        return ResponseEntity.ok(Map.of("token",jwtUtils.generateJwtToken(authenticationObject)));
    }


    public ResponseEntity<Map<String,String>> registerUser(SignupRequestModel requestObject){

        if(Boolean.TRUE.equals(userRepository.existsByEmail(requestObject.getEmail())))
        {
            throw new NullPointerException(requestObject.getEmail()+" Email is already exists.");
        }

        Set<RoleEntity> roles = new HashSet<>();

        if(requestObject.getRoles() == null)
        {
            roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER).orElseThrow(() -> new NullPointerException(ROLE_NOT_FOUND)));
        }
        else
        {
            for(String loopObject:requestObject.getRoles()){
                switch (loopObject){
                    case "admin":
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    case "guest":
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_GUEST).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    case "superAdmin":
                        Optional<UserEntity> checkAlreadySuperAdmin = userRepository.findUsersByRoleAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN);
                        if(checkAlreadySuperAdmin.isEmpty())
                            roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    default:
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;
                }
            }
        }

        if(roles.isEmpty()){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("message","Super Admin is already exists."));
        }
        UserEntity userEntity = new UserEntity(requestObject.getEmail(),requestObject.getEmail(),encoder.encode(requestObject.getPassword()),roles);
        userRepository.save(userEntity);

        return ResponseEntity.ok(Map.of("message","User registered successfully."));
    }


}
