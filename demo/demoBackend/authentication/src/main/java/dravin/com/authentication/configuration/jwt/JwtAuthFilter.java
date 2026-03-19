package dravin.com.authentication.configuration.jwt;

import dravin.com.authentication.service.loaduser.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger( JwtAuthFilter.class );

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final HandlerExceptionResolver handlerExceptionResolver;


    @Autowired
    public JwtAuthFilter(HandlerExceptionResolver exceptionResolver) {
        this.handlerExceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )throws ServletException, IOException {
        try {
            String jwt = this.jwtUtils.parseJwt( request );
            if ( jwt != null && jwtUtils.validateJwtToken( jwt ) ) {
                String username = jwtUtils.getUserNameFromJwtToken( jwt );

                UserDetails userDetails = userDetailsService.loadUserByUsername( username );
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( userDetails, null,userDetails.getAuthorities( ) );
                authentication.setDetails( new WebAuthenticationDetailsSource( ).buildDetails( request ) );

                SecurityContextHolder.getContext( ).setAuthentication( authentication );
            }
            filterChain.doFilter( request, response );
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException ex ) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
            logger.error("Authentication Failure: {}", ex.getMessage() );
        }
    }
}
