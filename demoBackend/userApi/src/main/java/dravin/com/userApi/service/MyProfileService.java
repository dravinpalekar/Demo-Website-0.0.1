package dravin.com.userApi.service;


import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.entity.UserOtherInformationEntity;
import dravin.com.repository.repository.UserRepository;
import dravin.com.userApi.configuration.jwt.JwtUtils;
import dravin.com.userApi.requestmodel.UpdateMyProfileRequestModel;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyProfileService {

    private static final Logger logger = LoggerFactory.getLogger(MyProfileService.class);

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final S3Client s3Client;

    @Value("${awsBucketName}")
    private String bucketName;

    @Value("${awsRegion}")
    private String awsRegion;

    public static final String MESSAGE = "message";
    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully.";
    public static final String DATA_NOT_FOUND = "Data not found";

    public MyProfileService(UserRepository userRepository, JwtUtils jwtUtils, S3Client s3Client) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.s3Client = s3Client;
    }

    public ResponseEntity<Map<String, String>> updateMyProfile(UpdateMyProfileRequestModel requestObject, MultipartFile file) throws IOException {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String tokenUserId = this.jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));

        Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId));
        if (userEntity.isPresent()) {

            UserEntity userEntityNew = userEntity.get();
            UserOtherInformationEntity existingInfo = userEntityNew.getUserOtherInformation();

            if (existingInfo == null) {
                existingInfo = new UserOtherInformationEntity();
            }

            if ((file != null)) {
                    String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID();
                    this.uploadImageIntoAws(file, tokenUserId, fileName);
                    existingInfo.setPhotoUrl(fileName);
            }

            existingInfo.setFirstName(requestObject.getFirstName());
            existingInfo.setMiddleName(requestObject.getMiddleName());
            existingInfo.setLastName(requestObject.getLastName());
            existingInfo.setGender(requestObject.getGender());
            existingInfo.setAge(requestObject.getAge());
            existingInfo.setCountry(requestObject.getCountry());
            existingInfo.setCity(requestObject.getCity());
            existingInfo.setPinCode(requestObject.getPinCode());
            existingInfo.setAddress(requestObject.getAddress());

            userEntityNew.setUserOtherInformation(existingInfo);
            userRepository.save(userEntityNew);
        }
        return ResponseEntity.ok(Map.of(MESSAGE, PROFILE_UPDATED_SUCCESSFULLY));
    }

    private void uploadImageIntoAws(MultipartFile file, String userId, String fileName) throws IOException {

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream()).size(100, 100).outputFormat(extension).toOutputStream(os);

        byte[] resizedImageBytes = os.toByteArray();

        String fileKey = "users/profile/" + userId + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(new ByteArrayInputStream(resizedImageBytes), resizedImageBytes.length));
    }

    public ResponseEntity<Map<String, Object>> getMyProfile() {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String tokenUserId = jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId)).orElseThrow(() -> new NullPointerException("User not found with ID: " + tokenUserId));
        if (user.getUserOtherInformation() != null)
            return ResponseEntity.ok(Map.of("data", user.getUserOtherInformation()));
        else
            return ResponseEntity.ok(Map.of("error", DATA_NOT_FOUND));
    }


    public ResponseEntity<Map<String, Object>> getMyImage() {

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String tokenUserId = jwtUtils.getIdFromJwtToken(this.jwtUtils.getJwtFromCookies(request.getRequest()));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId)).orElseThrow(() -> new NullPointerException("User not found with ID: " + tokenUserId));
        if (user.getUserOtherInformation() != null && user.getUserOtherInformation().getPhotoUrl() != null)
            try {

                String url = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/profile/" + tokenUserId + "/" + user.getUserOtherInformation().getPhotoUrl();
                return ResponseEntity.ok().body(Map.of(
                        "image", url,
                        "firstName", user.getUserOtherInformation().getFirstName(),
                        "middleName", user.getUserOtherInformation().getMiddleName() != null ? user.getUserOtherInformation().getMiddleName() : " ",
                        "lastName", user.getUserOtherInformation().getLastName(),
                        "gender", user.getUserOtherInformation().getGender()));

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        else
            return ResponseEntity.ok(Map.of("error", DATA_NOT_FOUND));
    }

}
