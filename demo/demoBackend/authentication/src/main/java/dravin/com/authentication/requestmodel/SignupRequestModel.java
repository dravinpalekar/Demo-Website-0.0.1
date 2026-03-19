package dravin.com.authentication.requestmodel;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private String password;

    private Set<String> roles;
}
