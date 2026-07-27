    package dravin.com.authentication.service;


    import dravin.com.authentication.configuration.jwt.JwtUtils;
    import dravin.com.authentication.requestmodel.LoginRequestModel;
    import dravin.com.authentication.requestmodel.SignupRequestModel;
    import dravin.com.repository.constant.enumConstant.Roles;
    import dravin.com.repository.entity.RoleEntity;
    import dravin.com.repository.entity.UserEntity;
    import dravin.com.repository.repository.RoleRepository;
    import dravin.com.repository.repository.UserRepository;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.test.util.ReflectionTestUtils;

    import java.util.Map;
    import java.util.Optional;
    import java.util.Set;

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

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
            ResponseEntity<Map<String, String>> response = authenticationService.authenticateUser(prepareLoginRequestFunction("admin@gamil.com", "Admin@123"));
            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertNotNull(response.getBody());
            assertEquals("jwt-token", response.getBody().get("token"));

            verify(authenticationManager).authenticate(any());
            verify(jwtUtils).generateJwtToken(authentication);
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

            assertEquals("User registered successfully.", response.getBody().get("message"));
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

            assertEquals("User registered successfully.", response.getBody().get("message"));
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

            assertEquals("User registered successfully.", response.getBody().get("message"));
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

            assertEquals("User registered successfully.", response.getBody().get("message"));
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

            assertEquals("Super Admin is already exists.", response.getBody().get("message"));

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
            assertEquals("User registered successfully.", response.getBody().get("message"));

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

    }
