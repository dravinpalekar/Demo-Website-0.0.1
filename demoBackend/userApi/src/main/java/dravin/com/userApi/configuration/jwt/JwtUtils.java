package dravin.com.userApi.configuration.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;

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
}
