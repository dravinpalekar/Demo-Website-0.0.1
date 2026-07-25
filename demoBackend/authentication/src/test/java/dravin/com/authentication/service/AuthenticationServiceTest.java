package dravin.com.authentication.service;


import dravin.com.authentication.configuration.jwt.JwtUtils;
import dravin.com.authentication.requestmodel.LoginRequestModel;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

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


    @Test
    @DisplayName("While User Login Should pass when both fields are valid")
    void authenticateUserShouldReturnJwtToken() {

        LoginRequestModel request = new LoginRequestModel();
        ReflectionTestUtils.setField(request, "userName", "admin");
        ReflectionTestUtils.setField(request, "password", "Admin@123");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        ResponseEntity<?> response = authenticationService.authenticateUser(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertNotNull(body);
        assertEquals("jwt-token", body.get("token"));

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("While User Login Should fail when user enter wrong password")
    void authenticateUserShouldThrowException_WhenCredentialsAreWrong() {

        LoginRequestModel request = new LoginRequestModel();
        ReflectionTestUtils.setField(request, "userName", "admin");
        ReflectionTestUtils.setField(request, "password", "Admin@123");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticateUser(request));

        verify(jwtUtils, never()).generateJwtToken(any());
    }
}
