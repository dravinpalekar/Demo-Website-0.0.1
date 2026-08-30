package dravin.com.userApi.configuration.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${jwtCookieName}")
    private String jwtCookieName;

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

    public String getIdFromJwtToken(String token) {

        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Integer.class).toString();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public boolean validateJwtToken(String authToken) {
        Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(authToken);
        return true;
    }

}
