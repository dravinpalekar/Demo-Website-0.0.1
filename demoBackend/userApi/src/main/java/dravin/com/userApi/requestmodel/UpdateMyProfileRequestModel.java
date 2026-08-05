package dravin.com.userApi.requestmodel;

import dravin.com.repository.constant.enumConstant.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

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

}
