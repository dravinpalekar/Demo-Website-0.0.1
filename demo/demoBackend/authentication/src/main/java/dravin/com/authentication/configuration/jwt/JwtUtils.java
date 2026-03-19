package dravin.com.authentication.configuration.jwt;

import dravin.com.authentication.service.loaduser.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${jwtExpirationInMillisecond}")
    private int jwtExpirationMillisecond;

    private static final String authorization = "Authorization";
    private static final String bearer = "Bearer ";

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // renamed from setSubject
                .claim("id", userPrincipal.getId())
                .claim("roles", userPrincipal.getAuthorities())
                .issuedAt(new Date()) // renamed from setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMillisecond)) // renamed from setExpiration
                .signWith(key(), Jwts.SIG.HS256) // Use Jwts.SIG instead of SignatureAlgorithm
                .compact();
    }

//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
//    }

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

        String headerAuth = request.getHeader( authorization );

        if ( StringUtils.hasText( headerAuth ) && headerAuth.startsWith( bearer ) ) {
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
