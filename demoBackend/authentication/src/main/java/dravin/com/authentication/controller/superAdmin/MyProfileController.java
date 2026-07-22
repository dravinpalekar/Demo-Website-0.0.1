package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.UpdateMyProfileRequestModel;
import dravin.com.authentication.service.superAdmin.MyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static dravin.com.authentication.constant.RoutesFile.*;

@RestController
@RequestMapping(API_SUPER_ADMIN + MY_PROFILE)
@Tag(name = "The profile controller will manage all types of other information related to the users' like full name, age, gender, address, country or display profile photo, and will be managed by the only super-admin.")
public class MyProfileController {

    private static final Logger logger = LoggerFactory.getLogger( MyProfileController.class );

    private final MyProfileService myProfileService;

    public MyProfileController(MyProfileService myProfileService) {
        this.myProfileService = myProfileService;
    }

    @PostMapping(value =CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Store other information of a users",
            description = "This API is for storing or updating users' other information, like full name, age, gender, address, country or display profile photo."
    )
    public ResponseEntity<?> updateMyProfile(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("updateMyProfileRequest") @Valid UpdateMyProfileRequestModel updateMyProfileRequest) {
        if (file != null && !file.isEmpty())
            this.validateImageFile(file);
        return myProfileService.updateMyProfile(updateMyProfileRequest,file);
    }

    @GetMapping(GET)
    @Operation(
            summary = "Getting other information of a user",
            description = "This API is for getting other information about a user, like full name, age, gender, address, and country."
    )
    public ResponseEntity<?> getMyProfile() {
        return myProfileService.getMyProfile();
    }

    @GetMapping(GET_MY_IMAGE)
    @Operation(
            summary = "Getting user image",
            description = "This API is for getting an image of the user and the image format in base64."
    )
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
