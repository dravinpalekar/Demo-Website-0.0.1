package dravin.com.authentication.service.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.ActivateRequestModel;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger( UserService.class );

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String,Object>> getAllUser(){

        return ResponseEntity.ok(Map.of("data", userRepository.findByDeletedAtIsNull()));
    }

    public ResponseEntity<Map<String,String>> deleteUserById(Long id){

        Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedAtIsNull(id);
        if(userEntity.isPresent()){
            userEntity.get().setDeletedAt(new Date());
            userRepository.save(userEntity.get());
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
        }else {
            return ResponseEntity.badRequest().body(Map.of("message","User not found"));
        }
    }

    public ResponseEntity<Map<String,String>> activeAndDeactivateUserByID(ActivateRequestModel requestModel){

        Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedAtIsNull(requestModel.getId());

        if(userEntity.isPresent()){
            userEntity.get().setActive(requestModel.getStatus());
            userRepository.save(userEntity.get());
            return ResponseEntity.ok(Map.of("message", "User "+ requestModel.getStatus().toString().toLowerCase()+" successfully."));
        }
        return ResponseEntity.badRequest().body(Map.of("message","User not found"));
    }
}
