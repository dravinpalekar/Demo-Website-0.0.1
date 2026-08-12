package dravin.com.restApi.configuration.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

import static dravin.com.restApi.constant.RoutesFile.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.setApplicationDestinationPrefixes(WEBSOCKET_APPLICATION_DESTINATION_PREFIX);
        config.enableSimpleBroker(WEBSOCKET_TOPIC, WEBSOCKET_PRIVATE);
        config.setUserDestinationPrefix(WEBSOCKET_USER_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_CONNECTION)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String currentUserName = accessor.getFirstNativeHeader("currentUserName");
                    String targetUserName = accessor.getFirstNativeHeader("targetUserName");

                    if (currentUserName != null && !currentUserName.isEmpty()) {
                        accessor.setUser(new StompPrincipal(currentUserName));
                        logger.info("set user in StompPrincipal - "+currentUserName);
                    }

                    if (currentUserName != null && !currentUserName.isBlank()) {
                        accessor.getSessionAttributes().put("currentUserName", currentUserName);
                    }
                    if (targetUserName != null && !targetUserName.isBlank()) {
                        accessor.getSessionAttributes().put("targetUserName", targetUserName);
                    }
                }
                return message;
            }
        });
    }
}

class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}