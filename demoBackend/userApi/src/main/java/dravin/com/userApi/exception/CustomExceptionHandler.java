package dravin.com.userApi.exception;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    private ProblemDetail errorDetail;

    private static final String ACCESS_DENIED_REASON = "accessDeniedReason";
    private static final String AUTHENTICATION_FAILURE = "Authentication Failure";
    private static final String JWT_TOKEN_ALREADY_EXPIRED = "JWT Token already expired";


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail signatureException(SignatureException ex) {

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

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail malformedJwtException(MalformedJwtException ex) {

        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ex.getMessage());
        errorDetail.setProperty(ACCESS_DENIED_REASON, AUTHENTICATION_FAILURE);
        return errorDetail;
    }

}
