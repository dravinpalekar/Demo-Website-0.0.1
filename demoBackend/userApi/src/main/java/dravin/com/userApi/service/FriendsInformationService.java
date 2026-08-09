package dravin.com.userApi.service;


import dravin.com.repository.constant.enumConstant.FriendStatus;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.FriendsInformationEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.FriendsInformationRepository;
import dravin.com.repository.repository.UserRepository;
import dravin.com.userApi.configuration.jwt.JwtUtils;
import dravin.com.userApi.requestmodel.NameRequestModel;
import dravin.com.userApi.responsemodel.GetAllUserListResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dravin.com.userApi.constant.ConstantString.*;
import static dravin.com.userApi.constant.Error.*;

@Service
public class FriendsInformationService {

    private static final Logger logger = LoggerFactory.getLogger(FriendsInformationService.class);

    private final UserRepository userRepository;
    private final S3Presigner s3Presigner;
    private final FriendsInformationRepository friendsInformationRepository;
    private final JwtUtils jwtUtils;

    @Value("${awsBucketName}")
    private String bucketName;

    @Value("${awsRegion}")
    private String awsRegion;


    public FriendsInformationService(UserRepository userRepository, S3Presigner s3Presigner, FriendsInformationRepository friendsInformationRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.s3Presigner = s3Presigner;
        this.friendsInformationRepository = friendsInformationRepository;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<Map<String,Object>> getPeopleList() {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String userIdFromToken = jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        List<Object[]> entityList = this.userRepository.findUsersByRoleAndActiveAndDeletedAtIsNull(Roles.ROLE_USER, Status.ENABLE);

        List<GetAllUserListResponseModel> responseData = new ArrayList<>();

        for (Object[] forLoopObject : entityList) {

            UserEntity userEntity = (UserEntity) forLoopObject[0];
            FriendsInformationEntity friendsInformationEntity = (FriendsInformationEntity) forLoopObject[1];

            if(Long.valueOf(userIdFromToken).equals(userEntity.getId()))
                continue;

            GetAllUserListResponseModel addToList = new GetAllUserListResponseModel();

            addToList.setFullName(userEntity.getUserOtherInformation().getFirstName() + " " + userEntity.getUserOtherInformation().getMiddleName() + " " + userEntity.getUserOtherInformation().getLastName());
            addToList.setEmail(userEntity.getEmail());
            addToList.setFullAddress(userEntity.getUserOtherInformation().getCity() + ", " + userEntity.getUserOtherInformation().getCountry());

            String photoUrl = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + userEntity.getId() + "/" + userEntity.getUserOtherInformation().getPhotoUrl();

            addToList.setPhotoData(photoUrl);

            if(friendsInformationEntity != null && friendsInformationEntity.getUserA().equals(userEntity) && friendsInformationEntity.getStatus().equals(FriendStatus.SEND))
                addToList.setFriendStatus(FriendStatus.ACCEPT);

            if(friendsInformationEntity != null && friendsInformationEntity.getUserB().equals(userEntity) && friendsInformationEntity.getStatus().equals(FriendStatus.SEND))
                addToList.setFriendStatus(FriendStatus.SEND);

            if(friendsInformationEntity != null && friendsInformationEntity.getStatus().equals(FriendStatus.FRIEND))
                addToList.setFriendStatus(FriendStatus.FRIEND);

            responseData.add(addToList);
        }
        return ResponseEntity.ok().body(Map.of("data", responseData));
    }

    public ResponseEntity<Map<String,String>> sendFriendRequest(NameRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String currentUserName = jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        List<UserEntity> userEntityList = this.userRepository.findByUserNameInAndDeletedAtIsNullAndActive(List.of(currentUserName, requestModel.getName()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;     UserEntity sendToUserEntity = null;
            for(UserEntity forLoopObject : userEntityList)
            {
                if(forLoopObject.getUserName().equals(currentUserName))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<FriendsInformationEntity> friendsInformationEntity = this.friendsInformationRepository.findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(currentUserEntity, sendToUserEntity, currentUserEntity, sendToUserEntity);

            if (friendsInformationEntity.isPresent() && friendsInformationEntity.get().getStatus().equals(FriendStatus.SEND)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE, FRIEND_REQUEST_ALREADY_SENT));
            } else {
                this.friendsInformationRepository.save(new FriendsInformationEntity(currentUserEntity, sendToUserEntity, FriendStatus.SEND));
                return ResponseEntity.ok(Map.of(MESSAGE, SUCCESSFULLY_FRIEND_REQUEST_SENT));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }

    public ResponseEntity<Map<String,String>> acceptFriendRequest(NameRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String currentUserName = jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        List<UserEntity> userEntityList = this.userRepository.findByUserNameInAndDeletedAtIsNullAndActive(List.of(currentUserName, requestModel.getName()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;     UserEntity sendToUserEntity = null;
            for(UserEntity forLoopObject : userEntityList)
            {
                if(forLoopObject.getUserName().equals(currentUserName))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<FriendsInformationEntity> friendsInformationEntity = this.friendsInformationRepository.findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(currentUserEntity, sendToUserEntity, currentUserEntity, sendToUserEntity);

            if (friendsInformationEntity.isPresent() && friendsInformationEntity.get().getStatus().equals(FriendStatus.SEND)) {

                friendsInformationEntity.get().setStatus(FriendStatus.FRIEND);
                this.friendsInformationRepository.save(friendsInformationEntity.get());
                return ResponseEntity.ok(Map.of(MESSAGE, SUCCESSFULLY_ACCEPTED_FRIEND_REQUEST_SENT));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, DATA_NOT_FOUND));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }


    // 2. GENERATE SECURE USER-SPECIFIC URL AND GIVEN TIME ALSO
    public String getFileUrlForUser(String fileKey, String requestingUserId) {
        // SECURITY CHECK: Validation ki file request karne wala user wahi hai ya nahi
        if (!fileKey.startsWith("users/" + requestingUserId + "/")) {
            throw new SecurityException("Unauthorized Access: Aap kisi aur user ki image access nahi kar sakte!");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(fileKey).build();

        // 15 Minutes ke expiry period ke sath Temporary URL banega
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1)) // URL sirf 15 minute chalega
                .getObjectRequest(getObjectRequest).build();

        // Pre-signed URL string me convert karke return karein
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
