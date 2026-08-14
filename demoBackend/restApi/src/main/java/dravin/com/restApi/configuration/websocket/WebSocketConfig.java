package dravin.com.restApi.configuration.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static dravin.com.restApi.constant.RoutesFile.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;

    public WebSocketConfig(WebSocketHandshakeInterceptor webSocketHandshakeInterceptor, WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor) {
        this.webSocketHandshakeInterceptor = webSocketHandshakeInterceptor;
        this.webSocketAuthChannelInterceptor = webSocketAuthChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.setApplicationDestinationPrefixes(WEBSOCKET_APPLICATION_DESTINATION_PREFIX);
        config.enableSimpleBroker(WEBSOCKET_TOPIC, WEBSOCKET_PRIVATE);
        config.setUserDestinationPrefix(WEBSOCKET_USER_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_CONNECTION)
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthChannelInterceptor);
    }
}