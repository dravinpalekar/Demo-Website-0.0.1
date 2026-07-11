package dravin.com.authentication.requestmodel.superAdmin;

import dravin.com.repository.constant.enumConstant.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateMyProfileRequestModel {

    @NotBlank(message = "Fist Name is mandatory.")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name is mandatory.")
    private String lastName;

    private Gender gender;

    @NotNull(message = "Age is mandatory.")
    @Min(value = 4, message = "Age must be at least 04.")
    @Max(value = 100, message = "Age must not be more than 100.")
    private Integer age;

    @NotBlank(message = "Country is mandatory.")
    private String country;

    @NotBlank(message = "City is mandatory.")
    private String city;

    @NotNull(message = "Pin-Code is mandatory.")
    @Min(value = 4, message = "Pin-Code must be at least 04.")
    @Max(value = 999999, message = "Pin-Code must not be more than 999999.")
    private Integer pinCode;

    @NotBlank(message = "Address is mandatory.")
    private String address;


//    private MultipartFile file;
//
//    @Pattern(regexp = "image/(jpeg|jpg|jpe|png)", message = "Only JPEG, JPG, JPE or PNG images are allowed")
//    private String contentType;
//
//    public void setFile(MultipartFile file) {
//        this.file = file;
//        if (file != null) {
//            this.contentType = file.getContentType();
//        }
//    }
}
