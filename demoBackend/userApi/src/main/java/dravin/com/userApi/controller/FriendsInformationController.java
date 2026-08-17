package dravin.com.userApi.controller;

import dravin.com.userApi.configuration.jwt.JwtUtils;
import dravin.com.userApi.requestmodel.IdRequestModel;
import dravin.com.userApi.requestmodel.NameRequestModel;
import dravin.com.userApi.service.FriendsInformationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static dravin.com.userApi.constant.RoutesFile.*;

@RestController
@RequestMapping(API_USER)
@Tag(name = "This controller is for handling Friends information between user to user like friend list, send request and accept request")
public class FriendsInformationController {

    private final JwtUtils jwtUtils;
    private final FriendsInformationService friendsInformationService;


    private static final Logger logger = LoggerFactory.getLogger(FriendsInformationController.class);

    public FriendsInformationController(JwtUtils jwtUtils, FriendsInformationService friendsInformationService) {
        this.jwtUtils = jwtUtils;
        this.friendsInformationService = friendsInformationService;
    }

    @GetMapping(PEOPLE_GET)
    public ResponseEntity<Map<String,Object>> getPeopleList(){

        return this.friendsInformationService.getPeopleList();
    }

    @PostMapping(SEND_REQUEST)
    public ResponseEntity<Map<String,String>> sendFriendRequest(@Valid @RequestBody IdRequestModel requestModel){

        return this.friendsInformationService.sendFriendRequest(requestModel);
    }

    @PostMapping(ACCEPT_REQUEST)
    public ResponseEntity<Map<String,String>> acceptFriendRequest(@Valid @RequestBody IdRequestModel requestModel){

        return this.friendsInformationService.acceptFriendRequest(requestModel);
    }

    @PostMapping(CANCEL_REQUEST)
    public ResponseEntity<?> cancelFriendRequest(@Valid @RequestBody IdRequestModel requestModel){

        return this.friendsInformationService.cancelFriendRequest(requestModel);
    }

    @GetMapping(FRIEND_GET)
    public ResponseEntity<?> getFriendList(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        return this.friendsInformationService.getFriendList(pageable);
    }

    @GetMapping(FRIEND_REQUEST_GET)
    public ResponseEntity<?> getFriendRequestNotification(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        return this.friendsInformationService.getFriendRequestNotification(pageable);
    }
}
