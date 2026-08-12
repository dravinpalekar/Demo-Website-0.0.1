package dravin.com.restApi.controller;

//import dravin.com.repository.repository.OneToOneRoomRepository;
//import dravin.com.repository.repository.RoomRepository;
//import dravin.com.repository.repository.UserRepository;
import dravin.com.restApi.configuration.jwt.JwtUtils;
import dravin.com.restApi.requestModel.ChatMessage;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

import static dravin.com.restApi.constant.RoutesFile.*;

@Controller
@RequestMapping(API_USER)
@Tag(name = "This controller is for handling chatting between user to user")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
//    private final RoomRepository roomRepository;
//    private final OneToOneRoomRepository oneToOneRoomRepository;
//    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public ChatController(SimpMessagingTemplate messagingTemplate,
//                          RoomRepository roomRepository, OneToOneRoomRepository oneToOneRoomRepository, UserRepository userRepository,
                          JwtUtils jwtUtils) {
        this.messagingTemplate = messagingTemplate;
//        this.roomRepository = roomRepository;
//        this.oneToOneRoomRepository = oneToOneRoomRepository;
//        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }


    @MessageMapping(ONE_TO_ONE_SEND_MESSAGE)
    public void sendPrivateMessage(@Payload ChatMessage msg) {

        messagingTemplate.convertAndSendToUser( msg.getRecipient(),WEBSOCKET_PRIVATE, msg );
//        logger.info("Message received from " + msg.getSender() + ": " + msg.getRecipient());
//        return msg;
    }

    @MessageMapping(ONE_TO_ONE_ADD_USER)
    public void addUser(@Payload ChatMessage msg, SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("username", msg.getSender());
//        logger.info("User joined: " + msg.getSender());
//        return msg;
    }

    @PostMapping("create/room")
    public ResponseEntity<?> createRoomForOneToOne(@RequestHeader("Authorization") String authHeader, @RequestBody String senderUserName) {

//        String token = authHeader.substring(7);
//        String userName = jwtUtils.getUserNameFromJwtToken(token);
//        String temp = senderUserName;
//
//        List<UserEntity> userList = userRepository.findByUserNameInAndDeletedAtIsNullAndActive(List.of(userName, temp), Status.ENABLE);
//
//        final String[] userA = new String[1];
//        String userB;
//        userList.forEach(userEntity -> {
//
//            userA[0] = userEntity.getUserName();
//        });

//        if(userA.isPresent() && userB.isPresent())
//        {
//            oneToOneRoomRepository.findByUserAAndUserB(userA.get(),userB.get());
//        }


        return ResponseEntity.ok(Map.of("token", "dddddddddd"));
    }


}


