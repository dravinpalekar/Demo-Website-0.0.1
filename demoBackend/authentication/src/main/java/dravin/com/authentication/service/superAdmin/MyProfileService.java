package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.configuration.jwt.JwtUtils;
import dravin.com.authentication.requestmodel.superAdmin.UpdateMyProfileRequestModel;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.entity.UserOtherInformationEntity;
import dravin.com.repository.repository.UserRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static dravin.com.authentication.constant.ConstantString.MESSAGE;
import static dravin.com.authentication.constant.ConstantString.PROFILE_UPDATED_SUCCESSFULLY;
import static dravin.com.authentication.constant.Error.DATA_NOT_FOUND;

@Service
public class MyProfileService {


    private static final Logger logger = LoggerFactory.getLogger(MyProfileService.class);

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public MyProfileService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<Map<String, String>> updateMyProfile(UpdateMyProfileRequestModel requestObject, MultipartFile file) {

        String token = this.jwtUtils.getIdFromJwtToken(this.jwtUtils.parseJwt(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));

        Optional<UserEntity> userEntity = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(token));
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

    public ResponseEntity<Map<String, Object>> getMyProfile() {

        String token = jwtUtils.getIdFromJwtToken(jwtUtils.parseJwt(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(token)).orElseThrow(() -> new NullPointerException("User not found with ID: " + token));
        if (user.getUserOtherInformation() != null)
            return ResponseEntity.ok(Map.of("data", user.getUserOtherInformation()));
        else
            return ResponseEntity.ok(Map.of("error", DATA_NOT_FOUND));
    }


    public ResponseEntity<Map<String, Object>> getMyImage() {
        String token = jwtUtils.getIdFromJwtToken(jwtUtils.parseJwt(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(Long.valueOf(token)).orElseThrow(() -> new NullPointerException("User not found with ID: " + token));
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
