package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.UpdateMyProfileRequestModel;
import dravin.com.authentication.service.superAdmin.MyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + MY_PROFILE)
public class MyProfileController {

    private static final Logger logger = LoggerFactory.getLogger( MyProfileController.class );

    private final MyProfileService myProfileService;

    public MyProfileController(MyProfileService myProfileService) {
        this.myProfileService = myProfileService;
    }

    @PostMapping(value =CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfile(@RequestParam("file") MultipartFile file, @RequestPart("updateMyProfileRequest") UpdateMyProfileRequestModel updateMyProfileRequest) {
        this.validateImageFile(file);
        return myProfileService.updateMyProfile(updateMyProfileRequest,file);
    }

    @GetMapping(GET)
    public ResponseEntity<?> getMyProfile() {
        return myProfileService.getMyProfile();
    }

    @GetMapping(GET_MY_IMAGE)
    public ResponseEntity<?> getMyImage() {
        return myProfileService.getMyImage();
    }

    private void validateImageFile(MultipartFile file) {

//        if (file.getSize() > 1_048_576) { // 1 MB
//            throw new IllegalArgumentException("File size must not exceed 1MB");
//        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(jpeg|jpg|jpe|png)")) {
            throw new IllegalArgumentException("Only JPEG, JPG, JPE or PNG images are allowed");
        }
    }
}
