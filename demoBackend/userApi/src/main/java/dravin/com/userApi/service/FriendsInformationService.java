package dravin.com.userApi.service;


import dravin.com.repository.constant.enumConstant.FriendStatus;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.FriendsInformationEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.FriendsInformationRepository;
import dravin.com.repository.repository.UserRepository;
import dravin.com.userApi.configuration.jwt.JwtUtils;
import dravin.com.userApi.requestmodel.IdRequestModel;
import dravin.com.userApi.responsemodel.GetAllUserListResponseModel;
import dravin.com.userApi.responsemodel.GetFriendListResponseModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.*;

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

    public ResponseEntity<Map<String, Object>> getPeopleList(Pageable pageable) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String userIdFromToken = jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        Page<UserEntity> entityList = this.userRepository.findUsersByRoleAndActiveAndDeletedAtIsNull(Roles.ROLE_USER, Long.valueOf(userIdFromToken), Status.ENABLE, pageable);

        List<GetAllUserListResponseModel> responseData = new ArrayList<>();

        for (UserEntity forLoopObject : entityList) {

            GetAllUserListResponseModel addToList = new GetAllUserListResponseModel();

            addToList.setId(forLoopObject.getId());
            addToList.setFullName(forLoopObject.getUserOtherInformation().getFirstName() + " " + forLoopObject.getUserOtherInformation().getMiddleName() + " " + forLoopObject.getUserOtherInformation().getLastName());
            addToList.setFullAddress(forLoopObject.getUserOtherInformation().getCity() + ", " + forLoopObject.getUserOtherInformation().getCountry());

            String photoUrl = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + forLoopObject.getId() + "/" + forLoopObject.getUserOtherInformation().getPhotoUrl();

            addToList.setPhotoData(photoUrl);
            responseData.add(addToList);
        }
        return ResponseEntity.ok().body(Map.of("data", responseData, "pageSize", entityList.getSize(), "getTotalElements", entityList.getTotalElements()));
    }

    public ResponseEntity<Map<String, String>> sendFriendRequest(IdRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long currentUserId = Long.valueOf(jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest())));

        List<UserEntity> userEntityList = this.userRepository.findByIdInAndDeletedAtIsNullAndActive(List.of(currentUserId, requestModel.getId()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;
            UserEntity sendToUserEntity = null;
            for (UserEntity forLoopObject : userEntityList) {
                if (forLoopObject.getId().equals(currentUserId))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<FriendsInformationEntity> friendsInformationEntity = this.friendsInformationRepository.findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(currentUserEntity, sendToUserEntity, currentUserEntity, sendToUserEntity);

            if (friendsInformationEntity.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE, FRIEND_REQUEST_ALREADY_SENT));
            } else {
                this.friendsInformationRepository.save(new FriendsInformationEntity(currentUserEntity, sendToUserEntity, FriendStatus.SEND));
                return ResponseEntity.ok(Map.of(MESSAGE, SUCCESSFULLY_FRIEND_REQUEST_SENT));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }

    public ResponseEntity<Map<String, String>> acceptFriendRequest(IdRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long currentUserId = Long.valueOf(jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest())));

        List<UserEntity> userEntityList = this.userRepository.findByIdInAndDeletedAtIsNullAndActive(List.of(currentUserId, requestModel.getId()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;     UserEntity sendToUserEntity = null;
            for (UserEntity forLoopObject : userEntityList) {
                if (forLoopObject.getId().equals(currentUserId))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<FriendsInformationEntity> friendsInformationEntity = this.friendsInformationRepository.findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(currentUserEntity, sendToUserEntity, currentUserEntity, sendToUserEntity);

            if (friendsInformationEntity.isPresent()) {

                friendsInformationEntity.get().setStatus(FriendStatus.FRIEND);
                this.friendsInformationRepository.save(friendsInformationEntity.get());
                return ResponseEntity.ok(Map.of(MESSAGE, SUCCESSFULLY_ACCEPTED_FRIEND_REQUEST_SENT));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, DATA_NOT_FOUND));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }


    public ResponseEntity<?> getFriendList(Pageable pageable) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String currentUserName = jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        Optional<UserEntity> userEntity = this.userRepository.findByUserNameAndDeletedAtIsNullAndActive(currentUserName, Status.ENABLE);

        if (userEntity.isPresent()) {
            Page<FriendsInformationEntity> friendsInformationEntities = this.friendsInformationRepository.findByUserAAndStatusAndDeletedAtIsNullOrUserBAndStatusAndDeletedAtIsNull(userEntity.get(), FriendStatus.FRIEND, userEntity.get(), FriendStatus.FRIEND, pageable);

            List<GetFriendListResponseModel> responseData = new ArrayList<>();

            for (FriendsInformationEntity forLoopObject : friendsInformationEntities) {

                GetFriendListResponseModel getFriendListResponseModel = new GetFriendListResponseModel();

                if (currentUserName.equals(forLoopObject.getUserA().getUserName())) {
                    getFriendListResponseModel.setId(forLoopObject.getUserB().getId());
                    getFriendListResponseModel.setFullName(forLoopObject.getUserB().getUserOtherInformation().getFirstName() + " " + forLoopObject.getUserB().getUserOtherInformation().getMiddleName() + " " + forLoopObject.getUserB().getUserOtherInformation().getLastName());
                    getFriendListResponseModel.setFullAddress(forLoopObject.getUserB().getUserOtherInformation().getCity() + ", " + forLoopObject.getUserB().getUserOtherInformation().getCountry());

                    String photoUrl = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + forLoopObject.getUserB().getId() + "/" + forLoopObject.getUserB().getUserOtherInformation().getPhotoUrl();
                    getFriendListResponseModel.setPhotoData(photoUrl);
                } else {
                    getFriendListResponseModel.setId(forLoopObject.getUserA().getId());
                    getFriendListResponseModel.setFullName(forLoopObject.getUserA().getUserOtherInformation().getFirstName() + " " + forLoopObject.getUserA().getUserOtherInformation().getMiddleName() + " " + forLoopObject.getUserA().getUserOtherInformation().getLastName());
                    getFriendListResponseModel.setFullAddress(forLoopObject.getUserA().getUserOtherInformation().getCity() + ", " + forLoopObject.getUserA().getUserOtherInformation().getCountry());

                    String photoUrl = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + forLoopObject.getUserA().getId() + "/" + forLoopObject.getUserA().getUserOtherInformation().getPhotoUrl();
                    getFriendListResponseModel.setPhotoData(photoUrl);
                }

                responseData.add(getFriendListResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(Map.of("data", responseData, "pageSize", friendsInformationEntities.getSize(), "getTotalElements", friendsInformationEntities.getTotalElements()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }


    public ResponseEntity<?> getFriendRequestNotification(Pageable pageable) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String currentUserName = jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        Optional<UserEntity> userEntity = this.userRepository.findByUserNameAndDeletedAtIsNullAndActive(currentUserName, Status.ENABLE);

        if (userEntity.isPresent()) {
            Page<FriendsInformationEntity> friendsInformationEntities = this.friendsInformationRepository.findByUserBAndStatusAndDeletedAtIsNull(userEntity.get(), FriendStatus.SEND, pageable);

            List<GetAllUserListResponseModel> responseData = new ArrayList<>();
            for (FriendsInformationEntity forLoopObject : friendsInformationEntities) {

                GetAllUserListResponseModel getAllUserListResponseModel = new GetAllUserListResponseModel();

                getAllUserListResponseModel.setId(forLoopObject.getUserA().getId());
                getAllUserListResponseModel.setFullName(forLoopObject.getUserA().getUserOtherInformation().getFirstName() + " " + forLoopObject.getUserA().getUserOtherInformation().getMiddleName() + " " + forLoopObject.getUserA().getUserOtherInformation().getLastName());
                getAllUserListResponseModel.setFullAddress(forLoopObject.getUserA().getUserOtherInformation().getCity() + ", " + forLoopObject.getUserA().getUserOtherInformation().getCountry());

                String photoUrl = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + forLoopObject.getUserA().getId() + "/" + forLoopObject.getUserA().getUserOtherInformation().getPhotoUrl();

                getAllUserListResponseModel.setPhotoData(photoUrl);
//                getAllUserListResponseModel.setFriendStatus(forLoopObject.getStatus());

                responseData.add(getAllUserListResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(Map.of("data", responseData, "pageSize", friendsInformationEntities.getSize(), "getTotalElements", friendsInformationEntities.getTotalElements()));

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

    public ResponseEntity<?> cancelFriendRequest(@Valid IdRequestModel requestModel) {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long currentUserId = Long.valueOf(jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest())));

        List<UserEntity> userEntityList = this.userRepository.findByIdInAndDeletedAtIsNullAndActive(List.of(currentUserId, requestModel.getId()), Status.ENABLE);

        if (userEntityList.size() == 2) {

            UserEntity currentUserEntity = null;
            UserEntity sendToUserEntity = null;
            for (UserEntity forLoopObject : userEntityList) {
                if (forLoopObject.getId().equals(currentUserId))
                    currentUserEntity = forLoopObject;
                else
                    sendToUserEntity = forLoopObject;
            }

            Optional<FriendsInformationEntity> friendsInformationEntity = this.friendsInformationRepository.findByUserAAndUserBAndStatusAndDeletedAtIsNull(sendToUserEntity, currentUserEntity, FriendStatus.SEND);

            if (friendsInformationEntity.isPresent()) {

                friendsInformationEntity.get().setDeletedAt(new Date());
                this.friendsInformationRepository.save(friendsInformationEntity.get());
                return ResponseEntity.ok(Map.of(MESSAGE, SUCCESSFULLY_CANCEL_FRIEND_REQUEST_SENT));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, DATA_NOT_FOUND));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, USER_NOT_FOUND));
    }
}
