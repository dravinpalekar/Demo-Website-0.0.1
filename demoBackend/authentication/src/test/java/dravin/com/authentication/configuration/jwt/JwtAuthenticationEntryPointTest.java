package dravin.com.authentication.configuration.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationEntryPointTest {

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private AuthenticationException authenticationException;

    @BeforeEach
    void setUp() {
        authenticationException = new BadCredentialsException("Invalid JWT Token");
    }

    @Test
    @DisplayName("We test jwt authentication entry point test")
    void commence_ShouldDelegateExceptionToHandlerExceptionResolver() {

        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        verify(handlerExceptionResolver, times(1)).resolveException(request, response, null, authenticationException);

        verifyNoMoreInteractions(handlerExceptionResolver);
    }
}
