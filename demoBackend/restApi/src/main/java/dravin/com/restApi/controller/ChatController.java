package dravin.com.restApi.controller;

import dravin.com.restApi.requestModel.ChatMessage;
import dravin.com.restApi.requestModel.UserIdRequestModel;
import dravin.com.restApi.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

import static dravin.com.restApi.constant.RoutesFile.*;

@RestController
@RequestMapping(API_USER)
@Tag(name = "This controller is for handling chatting between user to user")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping(ONE_TO_ONE_SEND_MESSAGE)
    public void sendMessageOneToOne(@Payload ChatMessage message, Principal principal) {

        this.chatService.sendMessageOneToOne(message, principal);
    }

    @MessageMapping(ONE_TO_ONE_ADD_USER)
    public void addUserForOneToOne(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor, Principal principal) {

        this.chatService.addUserForOneToOne(message, headerAccessor, principal);
    }

    @PostMapping(UPLOAD_FILE)
    public ResponseEntity<?> uploadImage(@RequestParam(value = "file") MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty())
            this.validateImageFile(file);
        return this.chatService.uploadImage(file);
    }

    @PostMapping(CREATE_ROOM)
    public ResponseEntity<?> createRoomForOneToOne(@Valid @RequestBody UserIdRequestModel requestModel) {

        return chatService.createRoomForOneToOne(requestModel);
    }

    private void validateImageFile(MultipartFile file) {

        if (file.getSize() > 1048576) { // 1 MB
            throw new MaxUploadSizeExceededException(1048576);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(jpeg|jpg|jpe|png)")) {
            throw new IllegalArgumentException("Only JPEG, JPG, JPE or PNG images are allowed");
        }
    }

}
