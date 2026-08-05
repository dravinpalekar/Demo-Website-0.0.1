package dravin.com.userApi.service;


import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.entity.UserOtherInformationEntity;
import dravin.com.repository.repository.UserRepository;
import dravin.com.userApi.configuration.jwt.JwtUtils;
import dravin.com.userApi.requestmodel.UpdateMyProfileRequestModel;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyProfileService {

    private static final Logger logger = LoggerFactory.getLogger(MyProfileService.class);

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public static final String MESSAGE = "message";
    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully.";
    public static final String DATA_NOT_FOUND = "Data not found";

    public MyProfileService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<Map<String, String>> updateMyProfile(UpdateMyProfileRequestModel requestObject, MultipartFile file, String headerToken) {

        String tokenUserId = this.jwtUtils.getIdFromJwtToken(headerToken.substring(7));

        Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId));
        if (userEntity.isPresent()) {

            UserEntity userEntityNew = userEntity.get();
            UserOtherInformationEntity existingInfo = userEntityNew.getUserOtherInformation();

            if (existingInfo == null) {
                existingInfo = new UserOtherInformationEntity();
            }

            if ((file != null)) {
                try {
                    String uploadDir = "uploadData/user/saveProfile/";

                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    String storeFileName = UUID.randomUUID().toString() + userEntityNew.getId();
                    String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
                    Path filePath = Paths.get(uploadDir + storeFileName);
                    Thumbnails.of(file.getInputStream()).size(200, 200).outputFormat(extension).toFile(filePath.toFile());

                    existingInfo.setPhotoUrl(storeFileName + '.' + extension);
                } catch (Exception e) {
                    return ResponseEntity.ok(Map.of("error", e.getMessage()));
                }
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

    public ResponseEntity<Map<String, Object>> getMyProfile(String headerToken) {

        String tokenUserId = jwtUtils.getIdFromJwtToken(headerToken.substring(7));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId)).orElseThrow(() -> new NullPointerException("User not found with ID: " + tokenUserId));
        if (user.getUserOtherInformation() != null)
            return ResponseEntity.ok(Map.of("data", user.getUserOtherInformation()));
        else
            return ResponseEntity.ok(Map.of("error", DATA_NOT_FOUND));
    }


    public ResponseEntity<Map<String, Object>> getMyImage(String headerToken) {
        String tokenUserId = jwtUtils.getIdFromJwtToken(headerToken.substring(7));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(tokenUserId)).orElseThrow(() -> new NullPointerException("User not found with ID: " + tokenUserId));
        if (user.getUserOtherInformation() != null && user.getUserOtherInformation().getPhotoUrl() != null)
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploadData/user/saveProfile/";
                Path filePath = Paths.get(uploadDir).resolve(user.getUserOtherInformation().getPhotoUrl()).normalize();

                if (!Files.exists(filePath)) {
                    return ResponseEntity.notFound().build();
                }

                byte[] imageBytes = Files.readAllBytes(filePath);
                String contentType = Files.probeContentType(filePath);
                String base64Image = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
                return ResponseEntity.ok().body(Map.of(
                        "image", base64Image,
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
