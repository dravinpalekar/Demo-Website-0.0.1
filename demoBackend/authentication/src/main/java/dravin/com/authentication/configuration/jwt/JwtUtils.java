package dravin.com.authentication.configuration.jwt;

import dravin.com.authentication.service.loaduser.UserDetailsImpl;
import dravin.com.repository.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static dravin.com.authentication.constant.ConstantString.AUTHORIZATION;
import static dravin.com.authentication.constant.ConstantString.BEARER;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${jwtExpirationInMillisecond}")
    private int jwtExpirationMillisecond;

    @Value("${jwtCookieName}")
    private String jwtCookieName;

    @Value("${jwtRefreshCookieName}")
    private String jwtRefreshCookieName;

    @Value("${jwtRefreshExpirationInMillisecond}")
    private long jwtRefreshExpirationInMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        String ssds = userPrincipal.getAuthorities().toString();

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // renamed from setSubject
                .claim("id", userPrincipal.getId())
                .claim("roles", userPrincipal.getAuthorities())
                .issuedAt(new Date()) // renamed from setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMillisecond)) // renamed from setExpiration
                .signWith(key(), Jwts.SIG.HS256) // Use Jwts.SIG instead of SignatureAlgorithm
                .compact();
    }

    // 2. JWT ko HTTP-Only ResponseCookie me Wrap karke Response me bhejne ke liye
    public ResponseCookie generateJwtCookie(Authentication authentication) {
        String jwt = generateJwtToken(authentication);
        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/")                     // Poori app ke endpoints par cookie bhejega
                .maxAge(jwtExpirationMillisecond / 1000) // Seconds me maxAge
                .httpOnly(true)                // XSS Defense: JS access band
                .secure(false)                 // Production me HTTPS par true rakhein
                .sameSite("Strict")            // CSRF Defense
                .build();
    }

    // 3. Logout ke waqt Cookie ko expire (clean) karne ke liye
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .path("/")
                .maxAge(0)                     // Immediate expire
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    // Refresh Token Cookie generate karne ke liye
    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return ResponseCookie.from(jwtRefreshCookieName, refreshToken)
                .path("/")
                .maxAge(jwtRefreshExpirationInMs / 1000)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    // Refresh Token Cookie clean/expire karne ke liye
    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookieName, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    // Request Cookie se Refresh Token extract karne ke liye
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // UserEntity se naya Access Token generate karne ke liye
    public String generateTokenFromUser(UserEntity user) {
        List<String> roles = user.getRole().stream()
                .map(role -> role.getName().name())
                .toList();

        return Jwts.builder()
                .subject(user.getUserName())
                .claim("id", user.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMillisecond))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    // UserEntity se naya Access Token Cookie generate karne ke liye
    public ResponseCookie generateJwtCookieFromUser(UserEntity user) {
        String jwt = generateTokenFromUser(user);
        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/")
                .maxAge(jwtExpirationMillisecond / 1000)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    // 4. Request Cookie se JWT Extract karne ka naya method (XSS Safe)
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    private SecretKey key() {
        // Use SecretKey instead of the generic Key interface
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String getUserNameFromJwtToken(String token) {

        return Jwts.parser()
                .verifyWith(key()) // replaced setSigningKey
                .build()
                .parseSignedClaims(token) // replaced parseClaimsJws
                .getPayload() // replaced getBody
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {

        Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(authToken);
        return true;
    }

    public String parseJwt( HttpServletRequest request ) {

        String headerAuth = request.getHeader( AUTHORIZATION );

        if ( StringUtils.hasText( headerAuth ) && headerAuth.startsWith( BEARER ) ) {
            return headerAuth.substring( 7 );
        }

        return null;
    }

    public String getIdFromJwtToken(String token) {

        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Integer.class).toString();
    }
}
