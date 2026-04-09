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

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class AuthenticationControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("While User Signup Should pass when all fields are valid")
    void validSignupRequest() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("test@example.com","Password123!"));
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("While User Signup Should fail when email is invalid")
    void invalidEmailFormat() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("not-an-email","Password123!"));
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short",                // Too short
            "nodigitpassword!",     // Missing digit
            "NoSpecialChar123",     // Missing special char
            "lowercase123!",        // Missing uppercase
            "UPPERCASE123!"         // Missing lowercase
    })
    @DisplayName("While User Signup Should fail when password does not meet complexity requirements")
    void invalidPasswordPatterns(String invalidPassword) {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("valid@email.com",invalidPassword));
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("While User Signup Should fail when both fields are blank")
    void blankFields() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction("",""));
        assertTrue(violations.size() >= 5);
    }

    @Test
    @DisplayName("While User Signup Should fail when both fields are null")
    void nullFields() {
        Set<ConstraintViolation<SignupRequestModel>> violations = validator.validate(prepareSignupRequestFunction(null,null));

        violations.forEach(e->{  //must not be blank for both the key
            assertEquals("must not be blank", e.getMessage());
        });
        assertFalse(violations.isEmpty());
    }

    private SignupRequestModel prepareSignupRequestFunction(String email, String password) {
        SignupRequestModel request = new SignupRequestModel();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private LoginRequestModel prepareLoginRequestFunction(String userName, String password) {
        LoginRequestModel request = new LoginRequestModel();
        ReflectionTestUtils.setField(request, "userName", userName);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }



    @Test
    @DisplayName("While User Login Should pass when all fields are valid")
    void whenAllFieldsAreValid_thenNoViolations() {
        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("dravin_user","securePass123"));
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("While User Login Should fail when userName is blank")
    void whenUserNameIsBlank_thenViolationOccurs() {
        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("","securePass123"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("userName") &&
                        v.getMessage().equals("User Name is mandatory."));
    }

    @Test
    @DisplayName("While User Login Should fail when password is blank")
    void whenPasswordIsBlank_thenViolationOccurs() {
        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(prepareLoginRequestFunction("dravin_user",""));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().equals("Password is mandatory."));
    }

    @Test
    @DisplayName("While User Login Should fail when both fields are null")
    void whenBothFieldsAreNull_thenMultipleViolationsOccur() {
        LoginRequestModel model = new LoginRequestModel();

        Set<ConstraintViolation<LoginRequestModel>> violations = validator.validate(model);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        tuple("userName", "User Name is mandatory."),
                        tuple("password", "Password is mandatory.")
                );
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }


}
