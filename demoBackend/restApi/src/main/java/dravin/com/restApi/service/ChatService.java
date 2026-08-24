package dravin.com.restApi.service;

import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.RoomEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.RoomRepository;
import dravin.com.repository.repository.UserRepository;
import dravin.com.restApi.configuration.jwt.JwtUtils;
import dravin.com.restApi.constant.ChatMessageType;
import dravin.com.restApi.constant.RoutesFile;
import dravin.com.restApi.requestModel.ChatMessage;
import dravin.com.restApi.requestModel.UserIdRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dravin.com.restApi.constant.ConstantString.MESSAGE;
import static dravin.com.restApi.constant.Error.USER_NOT_FOUND;

import static dravin.com.restApi.constant.RoutesFile.*;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final JwtUtils jwtUtils;
    private final S3Client s3Client;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    @Value("${awsBucketName}")
    private String bucketName;

    @Value("${awsRegion}")
    private String awsRegion;


    public ChatService(JwtUtils jwtUtils, S3Client s3Client, UserRepository userRepository, RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.jwtUtils = jwtUtils;
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

    public ResponseEntity<?> uploadImage(MultipartFile file) throws IOException {

        if ((file != null)) {
            boolean check = this.checkFileAlreadyExist("users/oneToOne/private/" +file.getOriginalFilename());
            if(!check)
                this.uploadImageIntoAws(file, file.getOriginalFilename());

            String url = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/oneToOne/private/" + file.getOriginalFilename();
            return ResponseEntity.ok().body(Map.of("data", url));
        }

        return ResponseEntity.ok().body(Map.of("data", "file is null"));
    }

    private boolean  checkFileAlreadyExist(String fileKey) {

        try {
            HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(fileKey).build();

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    private void uploadImageIntoAws(MultipartFile file, String fileName) throws IOException {

        String fileKey = "users/oneToOne/private/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileKey).contentType(file.getContentType()).build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public ResponseEntity<?> createRoomForOneToOne(UserIdRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Long currentUserIdToken = Long.valueOf(this.jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest())));

        List<UserEntity> userEntityList = this.userRepository.findByIdInAndDeletedAtIsNullAndActive(List.of(currentUserIdToken, requestModel.getId()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;     UserEntity sendToUserEntity = null;
            for (UserEntity forLoopObject : userEntityList) {
                if (forLoopObject.getId().equals(currentUserIdToken))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<RoomEntity> roomEntity = this.roomRepository.findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(currentUserEntity, sendToUserEntity, currentUserEntity, sendToUserEntity);

            if(roomEntity.isPresent())
            {
                return ResponseEntity.ok(Map.of("data", roomEntity.get().getRoomId()));
            }
            else
            {
               RoomEntity saveData = this.roomRepository.save(new RoomEntity(currentUserEntity, sendToUserEntity));
                return ResponseEntity.ok(Map.of("data", saveData.getRoomId()));
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }

    public void addUserForOneToOne(ChatMessage message, SimpMessageHeaderAccessor headerAccessor, Principal principal) {

        String currentUserId = principal.getName();

        // headerAccessor.getSessionAttributes().put("currentUserId", currentUserId);
        headerAccessor.getSessionAttributes().put("targetUserId", message.getRecipient());
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());

        // logger.info("User joined chat: {} -> {}", message.getSender(), message.getRecipient());

        checkBothUsersConnected(currentUserId, message.getRecipient(), message.getRoomId());

    }

    private void checkBothUsersConnected(String currentUserId, String targetUserId, String roomId) {

        String expectedDestination = "/user" + RoutesFile.WEBSOCKET_PRIVATE + "/" + roomId;

        boolean currentSubscribed = isUserSubscribedToDestination(currentUserId, expectedDestination);
        boolean targetSubscribed = isUserSubscribedToDestination(targetUserId, expectedDestination);

        // logger.info("Connection status -> {} : {} | {} : {}", currentUserId, currentSubscribed, targetUserId, targetSubscribed);

        if (currentSubscribed && targetSubscribed) {

            ChatMessage message = ChatMessage.builder().type(ChatMessageType.CONNECTED).roomId(roomId).content("You are now connected").build();

            messagingTemplate.convertAndSendToUser(currentUserId, WEBSOCKET_PRIVATE + "/" + roomId, message);
            messagingTemplate.convertAndSendToUser(targetUserId, WEBSOCKET_PRIVATE + "/" + roomId, message);

            // logger.info("Both users connected successfully: {} <-> {}", currentUserId, targetUserId);
        }
    }

    private boolean isUserSubscribedToDestination(String userId, String destination) {
        SimpUser simpUser = simpUserRegistry.getUser(userId);
        if (simpUser == null)
            return false;

        // Check karein user ke kisi bhi active session me ye destination subscribed hai
        // ya nahi
        return simpUser.getSessions().stream().flatMap(session -> session.getSubscriptions().stream()).anyMatch(sub -> destination.equals(sub.getDestination()));
    }

    public void sendMessageOneToOne(ChatMessage message, Principal principal) {
        message.setSender(principal.getName());

        messagingTemplate.convertAndSendToUser(message.getRecipient(), WEBSOCKET_PRIVATE + "/" + message.getRoomId(), message);
        // logger.info("Message received from " + msg.getSender() + ": " +
        // msg.getRecipient());
        // return msg;
    }
}
