package dravin.com.authentication.requestmodel;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequestModel {

    @NotBlank(message = "User Name is mandatory.")
    private String userName;

    @NotBlank(message = "Password is mandatory.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character."
    )
    private String password;
}
