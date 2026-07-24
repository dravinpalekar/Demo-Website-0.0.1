package dravin.com.authentication.controller;

import dravin.com.authentication.requestmodel.LoginRequestModel;
import dravin.com.authentication.requestmodel.SignupRequestModel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

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


    @AfterAll
    static void tearDown() {
        factory.close();
    }

}
