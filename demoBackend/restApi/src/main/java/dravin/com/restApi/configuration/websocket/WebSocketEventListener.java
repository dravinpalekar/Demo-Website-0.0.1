package dravin.com.restApi.configuration.websocket;


import dravin.com.restApi.constant.ChatMessageType;
import dravin.com.restApi.requestModel.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWsDisconnectListener( SessionDisconnectEvent event){
        //To listen to another even, create the another method with NewEvent as argument.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String currentUserId = (String) headerAccessor.getSessionAttributes().get("currentUserId");
        String targetUserId = (String) headerAccessor.getSessionAttributes().get("targetUserId");

        if(currentUserId !=null && targetUserId !=null ){

            logger.info("User disconnected: {} | Chat Partner: {}", currentUserId, targetUserId);

            ChatMessage message = ChatMessage.builder()
                    .type(ChatMessageType.LEAVE)
                    .sender(currentUserId)
                    .content("User lost connection")
                    .build();
            //pass the message to the broker specific topic : private
            messageSendingOperations.convertAndSendToUser(targetUserId, "/private", message);
        }
    }

}
