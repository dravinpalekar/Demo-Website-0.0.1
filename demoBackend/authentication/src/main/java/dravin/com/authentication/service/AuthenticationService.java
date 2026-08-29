package dravin.com.authentication.service;


import dravin.com.authentication.configuration.jwt.JwtUtils;
import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import dravin.com.authentication.service.loaduser.UserDetailsImpl;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.RoleRepository;
import dravin.com.repository.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static dravin.com.authentication.constant.ConstantString.*;
import static dravin.com.authentication.constant.Error.ROLE_NOT_FOUND;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

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

    public ResponseEntity<Map<String,Object>> authenticateUser(LoginRequestModel requestObject) {

        Authentication authenticationObject = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestObject.getUserName(), requestObject.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticationObject);

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authenticationObject.getPrincipal();
        List<String> roleList = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        String refreshToken = UUID.randomUUID().toString();

        userRepository.findByIdAndDeletedAtIsNull(userPrincipal.getId()).ifPresent(user -> {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        });

        Map<String,Object> responseObject = new HashMap<>();
        responseObject.put("roles",roleList);
        responseObject.put("userName",requestObject.getUserName());
        responseObject.put("id",userPrincipal.getId());
        responseObject.put("message","User logged in successfully.");

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(authenticationObject);
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        return ResponseEntity.ok().headers(headers).body(Map.of("data", responseObject));
    }


    public ResponseEntity<Map<String, String>> registerUser(SignupRequestModel requestObject) {

        if (Boolean.TRUE.equals(userRepository.existsByEmail(requestObject.getEmail()))) {
            throw new NullPointerException(requestObject.getEmail() + " Email is already exists.");
        }

        Set<RoleEntity> roles = new HashSet<>();

        if (requestObject.getRoles() == null) {
            roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER).orElseThrow(() -> new NullPointerException(ROLE_NOT_FOUND)));
        } else {
            for (String loopObject : requestObject.getRoles()) {
                switch (loopObject) {
                    case "admin":
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    case "guest":
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_GUEST).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    case "superAdmin":
                        Optional<UserEntity> checkAlreadySuperAdmin = userRepository.findUsersByRoleAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN);
                        if (checkAlreadySuperAdmin.isEmpty())
                            roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;

                    default:
                        roles.add(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER).orElseThrow(() -> new NullPointerException(loopObject + ROLE_NOT_FOUND)));
                        break;
                }
            }
        }

        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(MESSAGE, SUPER_ADMIN_IS_ALREADY_EXISTS));
        }
        UserEntity userEntity = new UserEntity(requestObject.getEmail(), requestObject.getEmail(), encoder.encode(requestObject.getPassword()), roles);
        userRepository.save(userEntity);

        return ResponseEntity.ok(Map.of(MESSAGE, USER_REGISTERED_SUCCESSFULLY));
    }

    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, "Refresh token is missing."));
        }

        Optional<UserEntity> userOptional = userRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(MESSAGE, "Invalid or expired refresh token. Please sign in again."));
        }

        UserEntity user = userOptional.get();

        String newRefreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUser(user);
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(newRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        List<String> roleList = user.getRole().stream().map(role -> role.getName().name()).toList();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("roles", roleList);
        responseData.put("userName", user.getUserName());
        responseData.put("id", user.getId());
        responseData.put("message", "Token refreshed successfully.");

        return ResponseEntity.ok().headers(headers).body(Map.of("data", responseData));
    }

    public ResponseEntity<Map<String, String>> logoutUser(HttpServletRequest request) {
        if (request != null) {
            String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
            if (refreshToken != null && !refreshToken.isBlank()) {
                userRepository.findByRefreshTokenAndDeletedAtIsNull(refreshToken).ifPresent(user -> {
                    user.setRefreshToken(null);
                    userRepository.save(user);
                });
            }
        }
        SecurityContextHolder.clearContext();
        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString());

        return ResponseEntity.ok().headers(headers).body(Map.of(MESSAGE, USER_LOGGED_OUT_SUCCESSFULLY));
    }

}
