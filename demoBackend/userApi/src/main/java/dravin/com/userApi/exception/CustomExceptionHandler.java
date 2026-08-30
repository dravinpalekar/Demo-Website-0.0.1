package dravin.com.userApi.exception;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    private ProblemDetail errorDetail;

    private static final String ACCESS_DENIED_REASON = "accessDeniedReason";
    private static final String AUTHENTICATION_FAILURE = "Authentication Failure";
    private static final String JWT_TOKEN_ALREADY_EXPIRED = "JWT Token already expired";
    private static final String ENUM_NOT_MATCH = "Enum did not match";


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail signatureException(SignatureException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail expiredJwtException(ExpiredJwtException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, JWT_TOKEN_ALREADY_EXPIRED);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail malformedJwtException(MalformedJwtException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonErrors(HttpMessageNotReadableException ex){

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_BAD_REQUEST), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, ENUM_NOT_MATCH);
        return errorDetail;
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errorsObject = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errorsObject.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return errorsObject;
    }

}
