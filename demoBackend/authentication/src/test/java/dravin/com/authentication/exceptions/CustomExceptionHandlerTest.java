package dravin.com.authentication.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Method;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomExceptionHandlerTest {

    private CustomExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CustomExceptionHandler();
    }

    // Dummy method used for MethodParameter
    public void dummyMethod(String name) {
    }

    @Test
    @DisplayName("methodArgumentNotValidException() should return validation errors")
    void testMethodArgumentNotValidException() throws Exception {

        Method method = getClass().getMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");

        bindingResult.addError(new FieldError("object", "name", "Name is required"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        Map<String, String> response = handler.methodArgumentNotValidException(exception);

        assertEquals(1, response.size());
        assertEquals("Name is required", response.get("name"));
    }

    @Test
    @DisplayName("handleJsonErrors() should return BAD_REQUEST")
    void testHandleJsonErrors() {

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON", new RuntimeException("Root Cause"), null);
        var result = handler.handleJsonErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Invalid JSON", result.getDetail());
        assertEquals("Enum did not match", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("SQLIntegrityConstraintViolationException()")
    void testSqlException() {

        SQLIntegrityConstraintViolationException ex = new SQLIntegrityConstraintViolationException("Duplicate Entry");

        var result = handler.sQLIntegrityConstraintViolationException(ex);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, result.getStatus());
        assertEquals("Duplicate entry not allowed", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("httpRequestMethodNotSupportedException()")
    void testMethodNotSupported() {

        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");

        var result = handler.httpRequestMethodNotSupportedException(ex);

        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, result.getStatus());
        assertEquals("Method Not Supported", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("noHandlerFoundException()")
    void testNoHandlerFoundException() {

        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/test", null);

        var result = handler.noHandlerFoundException(ex);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.getStatus());
        assertEquals("RESOURCE NOT FOUND", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("nullPointerException()")
    void testNullPointerException() {

        NullPointerException ex = new NullPointerException("Null value");

        Map<String, String> result = handler.nullPointerException(ex);

        assertEquals("Null value", result.get("error"));
    }

    @Test
    @DisplayName("malformedJwtException()")
    void testMalformedJwtException() {

        MalformedJwtException ex = new MalformedJwtException("Malformed JWT");

        var result = handler.malformedJwtException(ex);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, result.getStatus());
        assertEquals("Authentication Failure", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("authenticationException()")
    void testAuthenticationException() {

        InsufficientAuthenticationException ex = new InsufficientAuthenticationException("Authentication failed");

        var result = handler.authenticationException(ex);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, result.getStatus());
        assertEquals("Authentication Failure", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("expiredJwtException()")
    void testExpiredJwtException() {

        ExpiredJwtException ex = new ExpiredJwtException(null, null, "JWT Expired");

        var result = handler.expiredJwtException(ex);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, result.getStatus());
        assertEquals("JWT Token already expired", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("signatureException()")
    void testSignatureException() {

        SignatureException ex = new SignatureException("Invalid Signature");

        var result = handler.signatureException(ex);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, result.getStatus());
        assertEquals("JWT Signature not valid", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("accessDeniedException()")
    void testAccessDeniedException() {

        AccessDeniedException ex = new AccessDeniedException("Access denied");

        var result = handler.accessDeniedException(ex);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, result.getStatus());
        assertEquals("Not Authorized", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("badCredentialsException()")
    void testBadCredentialsException() {

        BadCredentialsException ex = new BadCredentialsException("Bad Credentials");

        var result = handler.badCredentialsException(ex);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, result.getStatus());
        assertEquals("Authentication Failure", result.getProperties().get("accessDeniedReason"));
    }

    @Test
    @DisplayName("handleMaxUpload()")
    void testHandleMaxUpload() {

        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024);

        var result = handler.handleMaxUpload(ex);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, result.getStatus());
        assertEquals("Maximum file size exceed", result.getProperties().get("message"));
    }
}
