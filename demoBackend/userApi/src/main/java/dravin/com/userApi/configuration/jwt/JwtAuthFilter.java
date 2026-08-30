package dravin.com.userApi.configuration.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtUtils jwtUtils;

    public JwtAuthFilter(HandlerExceptionResolver handlerExceptionResolver, JwtUtils jwtUtils) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = jwtUtils.getJwtFromCookies(request);


            if (jwt == null || jwt.isBlank()) {
                handlerExceptionResolver.resolveException(request, response, null, new SignatureException("Cookies Token is required."));
                return;
            }
            jwtUtils.validateJwtToken(jwt);
           
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
            logger.error("Authentication Failure: {}", ex.getMessage());
        }
    }
}