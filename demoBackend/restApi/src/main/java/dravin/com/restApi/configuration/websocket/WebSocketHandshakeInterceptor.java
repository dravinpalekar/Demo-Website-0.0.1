package dravin.com.restApi.configuration.websocket;


import dravin.com.restApi.configuration.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import jakarta.servlet.http.Cookie;

import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwtCookieName:jwt_token}")
    private String jwtCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (jwtCookieName.equals(cookie.getName())) {

                        String tokenUserName = jwtUtils.getUserNameFromJwtToken(cookie.getValue());
                        attributes.put("currentUserName", tokenUserName);
                    }
                }
            }
        }
        return true; // Connection allow karne ke liye true return karein
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Handshake complete hone ke baad execution (optional)
    }
}