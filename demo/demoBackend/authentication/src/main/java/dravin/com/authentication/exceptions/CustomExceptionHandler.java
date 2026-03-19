package dravin.com.authentication.exceptions;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger( CustomExceptionHandler.class );
    private static final String PARENT_EXCEPTION = "Parent Exception";
    private static final String ACCESS_DENIED_REASON = "accessDeniedReason";
    private static final String RESOURCE_NOT_FOUND = "RESOURCE NOT FOUND";
    private static final String METHOD_NOT_SUPPORTED = "Method Not Supported";
    private static final String AUTHENTICATION_FAILURE = "Authentication Failure";
    private static final String NOT_AUTHORIZED = "Not Authorized";
    private static final String ENUM_NOT_MATCH = "Enum did not match";
    private static final String DUPLICATE_ENTRY_NOT_ALLOWED = "Duplicate entry not allowed";
    private static final String JWT_SIGNATURE_NOT_VALID = "JWT Signature not valid";
    private static final String JWT_TOKEN_ALREADY_EXPIRED = "JWT Token already expired";

    private ProblemDetail errorDetail;

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errorsObject = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errorsObject.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return errorsObject;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonErrors(HttpMessageNotReadableException ex){

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_BAD_REQUEST), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, ENUM_NOT_MATCH);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ProblemDetail sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex){

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_BAD_REQUEST), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, DUPLICATE_ENTRY_NOT_ALLOWED);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_METHOD_NOT_ALLOWED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, METHOD_NOT_SUPPORTED);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail noHandlerFoundException(NoHandlerFoundException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_NOT_FOUND), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, RESOURCE_NOT_FOUND);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NullPointerException.class)
    public Map<String, String> nullPointerException(NullPointerException ex) {

        Map<String, String> errorsObject = new HashMap<>();
        errorsObject.put("error",ex.getMessage());
        return errorsObject;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail malformedJwtException(MalformedJwtException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail authenticationException(AuthenticationException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail expiredJwtException(ExpiredJwtException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_FORBIDDEN), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, JWT_TOKEN_ALREADY_EXPIRED);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail signatureException(SignatureException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_FORBIDDEN), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, JWT_SIGNATURE_NOT_VALID);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDeniedException(AccessDeniedException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_FORBIDDEN), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, NOT_AUTHORIZED);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail badCredentialsException(BadCredentialsException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }
}
