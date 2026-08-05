package dravin.com.userApi.service;


import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FriendsInformationService {

    private static final Logger logger = LoggerFactory.getLogger(FriendsInformationService.class);

    private final UserRepository userRepository;

    public FriendsInformationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String,Object>> getPeopleList(){

        List<UserEntity> entityList = this.userRepository.findUsersByRoleAndActiveAndDeletedAtIsNull(Roles.ROLE_USER, Status.ENABLE);

        return ResponseEntity.ok(Map.of("data",entityList));
    }

    public ResponseEntity<?> sendFriendRequest(){

        return ResponseEntity.ok(Map.of("data","entityList"));
    }
}
