package dravin.com.authentication.configuration.jwt;

import dravin.com.authentication.service.loaduser.UserDetailsImpl;
import io.jsonwebtoken.io.Encoders;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET = Encoders.BASE64.encode("12345678901234567890123456789012".getBytes());

    @BeforeEach
    void setUp() {

        jwtUtils = new JwtUtils();

        ReflectionTestUtils.setField(jwtUtils, "jwtSecretKey", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMillisecond", 60000);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateJwtToken() {

        UserDetailsImpl user = new UserDetailsImpl( 1L, "admin@mail.com" ,"admin@mail.com", "password", Collections.emptyList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should extract username from JWT token")
    void testGetUserNameFromJwtToken() {

        UserDetailsImpl user = new UserDetailsImpl(10L,"dravin.palekar@gmail.com","dravin.palekar@gmail.com","password", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken( user,null, user.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("dravin.palekar@gmail.com", username);
    }

    @Test
    @DisplayName("Should extract user id from JWT token")
    void testGetIdFromJwtToken() {

        UserDetailsImpl user = new UserDetailsImpl(99L,"admin@mail.com","admin@mail.com","password", Collections.emptyList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,null, user.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        String id = jwtUtils.getIdFromJwtToken(token);

        assertEquals("99", id);
    }

    @Test
    @DisplayName("Should validate valid JWT token")
    void testValidateJwtToken() {

        UserDetailsImpl user = new UserDetailsImpl(1L,"dravin.palekar@gmail.com", "dravin.palekar@gmail.com", "password", Collections.emptyList());

        Authentication authentication = new UsernamePasswordAuthenticationToken( user,null, user.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    @DisplayName("Should return token when Authorization header contains Bearer token")
    void testParseJwtSuccess() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.xyz");

        String token = jwtUtils.parseJwt(request);

        assertEquals("abc.def.xyz", token);
    }

    @Test
    @DisplayName("Should return null when Authorization header is missing")
    void testParseJwtHeaderMissing() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(jwtUtils.parseJwt(request));
    }

    @Test
    @DisplayName("Should return null when Authorization header is empty")
    void testParseJwtHeaderEmpty() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("");

        assertNull(jwtUtils.parseJwt(request));
    }

    @Test
    @DisplayName("Should return null when Authorization header does not start with Bearer")
    void testParseJwtWithoutBearer() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Basic xxxxxx");

        assertNull(jwtUtils.parseJwt(request));
    }

    @Test
    @DisplayName("Should throw exception for invalid JWT token")
    void testValidateJwtTokenInvalid() {

        assertThrows(Exception.class, () -> jwtUtils.validateJwtToken("invalid.token"));
    }

    @Test
    @DisplayName("Should throw exception while extracting username from invalid token")
    void testGetUsernameInvalidToken() {

        assertThrows(Exception.class, () -> jwtUtils.getUserNameFromJwtToken("invalid.token"));
    }

    @Test
    @DisplayName("Should throw exception while extracting id from invalid token")
    void testGetIdInvalidToken() {

        assertThrows(Exception.class, () -> jwtUtils.getIdFromJwtToken("invalid.token"));
    }
}
