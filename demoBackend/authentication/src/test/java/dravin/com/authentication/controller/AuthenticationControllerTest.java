package dravin.com.authentication.controller;

import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import dravin.com.authentication.service.AuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import static dravin.com.authentication.constant.RoutesFile.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Map;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private JsonMapper objectMapper;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("While User Login Should pass when all fields are valid")
    void validationSuccess() {

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("validUser", "Valid@1234"));
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    @DisplayName("While User Login Should fail when userName is blank")
    void validationBlankUserNameFails() {

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("", "Valid@1234"));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("userName") && v.getMessage().equals("User Name is mandatory.")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short1!",       // Less than 8 characters (7 chars)
            "lowercase1!",   // Missing uppercase
            "UPPERCASE1!",   // Missing lowercase
            "NoSpecial123",  // Missing special character
            "NoNumber@!"     // Missing digit
    })
    @DisplayName("While User Login Should fail validation for invalid password formats")
    void validationInvalidPasswordPatternFails(String invalidPassword) {

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("validUser", invalidPassword));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password") && v.getMessage().equals("Password must contain uppercase, lowercase, number and special character.")));
    }

    @Test
    @DisplayName("While User Login Should fail validation when password is blank")
    void validationBlankPasswordFails() {

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("validUser", ""));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password") && (
                        v.getMessage().equals("Password must contain uppercase, lowercase, number and special character.") ||
                                v.getMessage().equals("Password is mandatory.") ||
                                v.getMessage().equals("Password must be between 8 and 255 characters."))));
    }

    @Test
    @DisplayName("While User Login Should fail when both fields are null")
    void whenBothFieldsAreNullThenMultipleViolationsOccur() {
        LoginRequestModel model = new LoginRequestModel();

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(model);
        assertEquals(2, violations.size(), "Should have exactly 2 validation errors");

        boolean hasUserNameError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userName")
                && v.getMessage().equals("User Name is mandatory."));

        boolean hasPasswordError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")
                && v.getMessage().equals("Password is mandatory."));

        assertTrue(hasUserNameError, "Missing error message for userName");
        assertTrue(hasPasswordError, "Missing error message for password");
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
    @DisplayName("While User Signup Should pass when all fields are valid")
    void validSignupRequest() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("test@example.com", "Password123!"));
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    @DisplayName("While User Signup Should fail when userName is blank")
    void validationSignUpBlankUserNameFails() {

        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("", "Password123!"));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") && v.getMessage().equals("Email is mandatory.")));
    }

    @Test
    @DisplayName("While User Signup Should fail validation when password is blank")
    void validationSignUpBlankPasswordFails() {

        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("validUser", ""));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password") && (
                        v.getMessage().equals("Password must contain uppercase, lowercase, number and special character.") ||
                                v.getMessage().equals("Password is mandatory.") ||
                                v.getMessage().equals("Password must be between 8 and 255 characters."))));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short1!",       // Less than 8 characters (7 chars)
            "lowercase1!",   // Missing uppercase
            "UPPERCASE1!",   // Missing lowercase
            "NoSpecial123",  // Missing special character
            "NoNumber@!"     // Missing digit
    })
    @DisplayName("While User Signup Should fail validation for invalid password formats")
    void validationSignUpInvalidPasswordPatternFails(String invalidPassword) {

        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("test@example.com", invalidPassword));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password") && v.getMessage().equals("Password must contain uppercase, lowercase, number and special character.")));
    }

    @Test
    @DisplayName("While User Signup Should fail when email is invalid")
    void invalidEmailFormat() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("not-an-email", "Password123!"));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                v.getMessage().equals("must be a well-formed email address")));
    }

    @Test
    @DisplayName("While User Signup Should fail when both fields are null")
    void whenSignUpBothFieldsAreNullThenMultipleViolationsOccur() {
        SignupRequestModel model = new SignupRequestModel();

        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(model);
        assertEquals(2, violations.size(), "Should have exactly 2 validation errors");

        boolean hasUserNameError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")
                && v.getMessage().equals("Email is mandatory."));

        boolean hasPasswordError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")
                && v.getMessage().equals("Password is mandatory."));

        assertTrue(hasUserNameError, "Missing error message for email");
        assertTrue(hasPasswordError, "Missing error message for password");
    }

    @Test
    @DisplayName("While User Signup Should pass when all fields are valid with role name")
    void validSignupRequestWithRole() {

        SignupRequestModel request = new SignupRequestModel();
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "password", "Password123!");
        ReflectionTestUtils.setField(request, "roles", Set.of("superAdmin"));

        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }


    @Test
    @DisplayName("While User login Should authenticate user successfully")
    void authenticateUserSuccess() throws Exception {

        ResponseEntity<Map<String,String>> response = ResponseEntity.ok(Map.of("token", "jwt-token"));

        Mockito.doReturn(response).when(authenticationService).authenticateUser(any(LoginRequestModel.class));

        mockMvc.perform(post(API_AUTH + SIGN_IN).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prepareLoginRequestFunction("admin@gmail.com", "Admin@123"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authenticationService).authenticateUser(any(LoginRequestModel.class));
    }

    @Test
    @DisplayName("While User login Should return 406 when login request is invalid or wrong password")
    void authenticateUserInvalidRequest() throws Exception {

        mockMvc.perform(post(API_AUTH + SIGN_IN).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prepareLoginRequestFunction("admin.com", "adm123"))))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("While User signUp Should register user successfully")
    void registerUser_Success() throws Exception {

        SignupRequestModel request = prepareSignupRequestFunction("admin@mail.com","Admin@123");
        ReflectionTestUtils.setField(request, "roles", Set.of("user"));

        when(authenticationService.registerUser(any(SignupRequestModel.class))).thenReturn(ResponseEntity.ok(Map.of("message", "User registered successfully.")));

        mockMvc.perform(post(API_AUTH + SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("User registered successfully."));

        verify(authenticationService).registerUser(any(SignupRequestModel.class));
    }

    @Test
    @DisplayName("While User signUp Should return 400 when signup request is invalid")
    void registerUser_InvalidRequest() throws Exception {

        mockMvc.perform(post(API_AUTH + SIGN_UP).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prepareSignupRequestFunction("admin@mail.com","admin23"))))
                .andExpect(status().isNotAcceptable());
    }



    @AfterAll
    static void tearDown() {
        factory.close();
    }

}
