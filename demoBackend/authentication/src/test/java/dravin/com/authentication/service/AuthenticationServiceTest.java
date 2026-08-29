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
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseCookie;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.test.util.ReflectionTestUtils;

    import java.util.Collections;
    import java.util.Map;
    import java.util.Optional;
    import java.util.Set;

    import static dravin.com.authentication.constant.ConstantString.SUPER_ADMIN_IS_ALREADY_EXISTS;
    import static dravin.com.authentication.constant.ConstantString.USER_LOGGED_OUT_SUCCESSFULLY;
    import static dravin.com.authentication.constant.ConstantString.USER_REGISTERED_SUCCESSFULLY;
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    public class AuthenticationServiceTest {

        @InjectMocks
        private AuthenticationService authenticationService;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private JwtUtils jwtUtils;

        @Mock
        private Authentication authentication;

        @Mock
        private UserRepository userRepository;

        @Mock
        private RoleRepository roleRepository;

        @Mock
        private PasswordEncoder encoder;

        @Test
        @DisplayName("While User Login Should pass when both fields are valid")
        void authenticateUserShouldReturnJwtToken() {

            UserDetailsImpl user = new UserDetailsImpl(1L, "admin@gamil.com", "admin@gamil.com", "Admin@123", Collections.emptyList());
            when(authentication.getPrincipal()).thenReturn(user);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtUtils.generateJwtCookie(authentication)).thenReturn(ResponseCookie.from("jwt_token", "token").build());
            when(jwtUtils.generateRefreshJwtCookie(anyString())).thenReturn(ResponseCookie.from("refresh_token", "refresh").build());
            when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(new UserEntity()));

            ResponseEntity<Map<String, Object>> response = authenticationService.authenticateUser(prepareLoginRequestFunction("admin@gamil.com", "Admin@123"));
            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("data"));

            verify(authenticationManager).authenticate(any());
            verify(jwtUtils).generateJwtCookie(authentication);
            verify(jwtUtils).generateRefreshJwtCookie(anyString());
        }

        @Test
        @DisplayName("While User Login Should fail when user enter wrong password")
        void authenticateUserShouldThrowExceptionWhenCredentialsAreWrong() {

            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
            assertThrows(BadCredentialsException.class, () -> authenticationService.authenticateUser(prepareLoginRequestFunction("admin@gamil.com", "Admin@123")));

            verify(jwtUtils, never()).generateJwtToken(any());
        }

        private LoginRequestModel prepareLoginRequestFunction(String userName, String password) {
            LoginRequestModel request = new LoginRequestModel();
            ReflectionTestUtils.setField(request, "userName", userName);
            ReflectionTestUtils.setField(request, "password", password);
            return request;
        }

        private SignupRequestModel prepareSignupRequestFunction(String email, String password) {
            SignupRequestModel request = new SignupRequestModel();
            ReflectionTestUtils.setField(request, "email", email);
            ReflectionTestUtils.setField(request, "password", password);
            return request;
        }

        @Test
        @DisplayName("While User SignUp Should throw exception when email already exists")
        void registerUserEmailAlreadyExists() {

            when(userRepository.existsByEmail(anyString())).thenReturn(true);
            NullPointerException exception = assertThrows(NullPointerException.class,() -> authenticationService.registerUser(prepareSignupRequestFunction("admin@gamil.com","Admin@123")));
            assertTrue(exception.getMessage().contains("Email is already exists"));
            assertThrows(NullPointerException.class,() -> authenticationService.registerUser(prepareSignupRequestFunction("admin@gamil.com","Admin@123")));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("While User SignUp Should register user with default ROLE_USER")
        void registerUserDefaultRole() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", null);

            RoleEntity role = new RoleEntity();
            role.setName(Roles.ROLE_USER);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER)).thenReturn(Optional.of(role));
            when(encoder.encode(anyString())).thenReturn("encodedPassword");

            ResponseEntity<Map<String,String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertEquals(USER_REGISTERED_SUCCESSFULLY, response.getBody().get("message"));
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("While User SignUp Should register ROLE_ADMIN")
        void registerUserAdminRole() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("admin"));
            RoleEntity role = new RoleEntity();
            role.setName(Roles.ROLE_ADMIN);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.of(role));
            when(encoder.encode(anyString())).thenReturn("encodedPassword");

            ResponseEntity<Map<String, String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertEquals(USER_REGISTERED_SUCCESSFULLY, response.getBody().get("message"));
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("While User SignUp Should register ROLE_GUEST")
        void registerUserGuestRole() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("guest"));
            RoleEntity role = new RoleEntity();
            role.setName(Roles.ROLE_GUEST);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_GUEST)).thenReturn(Optional.of(role));
            when(encoder.encode(anyString())).thenReturn("encodedPassword");

            ResponseEntity<Map<String, String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertEquals(USER_REGISTERED_SUCCESSFULLY, response.getBody().get("message"));
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("While User SignUp Should register ROLE_SUPER_ADMIN when not exists")
        void registerUserSuperAdminNotExists() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("superAdmin"));
            RoleEntity role = new RoleEntity();
            role.setName(Roles.ROLE_SUPER_ADMIN);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.findUsersByRoleAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN)).thenReturn(Optional.empty());
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN)).thenReturn(Optional.of(role));
            when(encoder.encode(anyString())).thenReturn("encodedPassword");

            ResponseEntity<Map<String, String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertEquals(USER_REGISTERED_SUCCESSFULLY, response.getBody().get("message"));
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("While User SignUp Should return METHOD_NOT_ALLOWED when ROLE_SUPER_ADMIN already exists")
        void registerUserSuperAdminAlreadyExists() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("superAdmin"));

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.findUsersByRoleAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN)).thenReturn(Optional.of(new UserEntity()));
            ResponseEntity<Map<String, String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());

            assertEquals(SUPER_ADMIN_IS_ALREADY_EXISTS, response.getBody().get("message"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("While User SignUp Should assign ROLE_USER for unknown role")
        void registerUserInvalidRole() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("xyz"));
            RoleEntity role = new RoleEntity();
            role.setName(Roles.ROLE_USER);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER)).thenReturn(Optional.of(role));
            when(encoder.encode(anyString())).thenReturn("encodedPassword");
            ResponseEntity<Map<String, String>> response = authenticationService.registerUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(USER_REGISTERED_SUCCESSFULLY, response.getBody().get("message"));

            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("While User SignUp Should register user with default ROLE_USER but role not found in the database")
        void registerUserDefaultRoleDatabase() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", null);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER)).thenReturn(Optional.empty());

            assertThrows(NullPointerException.class, () -> authenticationService.registerUser(request));
        }

        @Test
        @DisplayName("While User SignUp Should register user with ROLE_ADMIN but role not found in the database")
        void registerUserRoleAdminDatabase() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("admin"));

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());

            assertThrows(NullPointerException.class, () -> authenticationService.registerUser(request));
        }

        @Test
        @DisplayName("While User SignUp Should register user with ROLE_GUEST but role not found in the database")
        void registerUserRoleGuestDatabase() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("guest"));

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_GUEST)).thenReturn(Optional.empty());

            assertThrows(NullPointerException.class, () -> authenticationService.registerUser(request));
        }

        @Test
        @DisplayName("While User SignUp Should assign ROLE_USER for unknown role but role not found in the database")
        void registerUserInvalidRoleDatabase() {
            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("xyz"));

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_USER)).thenReturn(Optional.empty());

            assertThrows(NullPointerException.class, () -> authenticationService.registerUser(request));
        }

        @Test
        @DisplayName("While User SignUp Should assign ROLE_SUPER_ADMIN for unknown role but role not found in the database")
        void registerUserInvalidRoleSuperAdminDatabase() {

            SignupRequestModel request = prepareSignupRequestFunction("admin@gamil.com","Admin@123");
            ReflectionTestUtils.setField(request, "roles", Set.of("superAdmin"));

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.findUsersByRoleAndDeletedAtIsNull(Roles.ROLE_SUPER_ADMIN)).thenReturn(Optional.empty());

            assertThrows(NullPointerException.class, () -> authenticationService.registerUser(request));
        }

        @Test
        @DisplayName("While User Logout Should logout successfully and clear cookie")
        void logoutUserSuccess() {

            HttpServletRequest request = mock(HttpServletRequest.class);
            ResponseCookie cleanCookie = ResponseCookie.from("jwt_token", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            ResponseCookie cleanRefreshCookie = ResponseCookie.from("refresh_token", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn("sample-refresh-token");
            when(userRepository.findByRefreshTokenAndDeletedAtIsNull("sample-refresh-token")).thenReturn(Optional.of(new UserEntity()));
            when(jwtUtils.getCleanJwtCookie()).thenReturn(cleanCookie);
            when(jwtUtils.getCleanJwtRefreshCookie()).thenReturn(cleanRefreshCookie);

            ResponseEntity<Map<String, String>> response = authenticationService.logoutUser(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(USER_LOGGED_OUT_SUCCESSFULLY, response.getBody().get("message"));

            verify(jwtUtils).getCleanJwtCookie();
            verify(jwtUtils).getCleanJwtRefreshCookie();
        }

        @Test
        @DisplayName("While User Refresh Token Should refresh token successfully")
        void refreshTokenSuccess() {

            HttpServletRequest request = mock(HttpServletRequest.class);
            UserEntity user = new UserEntity("admin@mail.com", "admin", "password", Collections.emptySet());
            ReflectionTestUtils.setField(user, "id", 1L);

            when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn("valid-refresh-token");
            when(userRepository.findByRefreshTokenAndDeletedAtIsNull("valid-refresh-token")).thenReturn(Optional.of(user));
            when(jwtUtils.generateJwtCookieFromUser(user)).thenReturn(ResponseCookie.from("jwt_token", "new-jwt").build());
            when(jwtUtils.generateRefreshJwtCookie(anyString())).thenReturn(ResponseCookie.from("refresh_token", "new-refresh").build());

            ResponseEntity<Map<String, Object>> response = authenticationService.refreshToken(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody().get("data"));
            Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
            assertNotNull(data.get("roles"));
            assertEquals("admin", data.get("userName"));
            assertEquals(1L, data.get("id"));

            verify(userRepository).save(user);
            verify(jwtUtils).generateJwtCookieFromUser(user);
        }

        @Test
        @DisplayName("While User Refresh Token Should return BAD_REQUEST when refresh token is missing")
        void refreshTokenMissing() {

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn(null);

            ResponseEntity<Map<String, Object>> response = authenticationService.refreshToken(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("While User Refresh Token Should return FORBIDDEN when refresh token is invalid or not in DB")
        void refreshTokenInvalid() {

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn("invalid-token");
            when(userRepository.findByRefreshTokenAndDeletedAtIsNull("invalid-token")).thenReturn(Optional.empty());

            ResponseEntity<Map<String, Object>> response = authenticationService.refreshToken(request);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }

    }
