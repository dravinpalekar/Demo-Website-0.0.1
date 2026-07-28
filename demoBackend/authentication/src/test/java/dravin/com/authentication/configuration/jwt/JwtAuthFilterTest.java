package dravin.com.authentication.configuration.jwt;

import dravin.com.authentication.service.loaduser.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import io.jsonwebtoken.security.SignatureException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        ReflectionTestUtils.setField(jwtAuthFilter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(jwtAuthFilter, "userDetailsService", userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate user when JWT is valid")
    void testDoFilterInternalValidJwt() throws ServletException, IOException {

        String jwt = "valid-token";
        String username = "admin";

        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(jwtUtils.parseJwt(request)).thenReturn(jwt);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthFilter.doFilter(request, response, filterChain);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain when JWT is null")
    void testDoFilterInternalNullJwt() throws ServletException, IOException {

        when(jwtUtils.parseJwt(request)).thenReturn(null);

        jwtAuthFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain when JWT is invalid")
    void testDoFilterInternalInvalidJwt() throws ServletException, IOException {

        String jwt = "invalid-token";

        when(jwtUtils.parseJwt(request)).thenReturn(jwt);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

        jwtAuthFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should resolve ExpiredJwtException")
    void testDoFilterInternalExpiredJwtException() throws ServletException, IOException {

        ExpiredJwtException exception = mock(ExpiredJwtException.class);

        when(jwtUtils.parseJwt(request)).thenThrow(exception);
        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(request, response, null, exception);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should resolve SignatureException")
    void testDoFilterInternalSignatureException() throws ServletException, IOException {

        SignatureException exception = mock(SignatureException.class);

        when(jwtUtils.parseJwt(request)).thenThrow(exception);
        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(request, response, null, exception);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should resolve MalformedJwtException")
    void testDoFilterInternalMalformedJwtException() throws ServletException, IOException {

        MalformedJwtException exception = new MalformedJwtException("Malformed JWT");

        when(jwtUtils.parseJwt(request)).thenThrow(exception);
        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(request, response, null, exception);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
