package dravin.com.authentication.requestmodel;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignupRequestModel {

    @NotBlank
    @Size(min = 8, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;

    private Set<String> roles;
}
