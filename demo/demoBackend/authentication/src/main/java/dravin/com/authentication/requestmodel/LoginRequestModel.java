package dravin.com.authentication.requestmodel;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestModel {

    @NotBlank(message = "User Name is mandatory.")
    private String userName;

    @NotBlank(message = "Password is mandatory.")
    private String password;
}
