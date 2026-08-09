package dravin.com.userApi.requestmodel;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NameRequestModel {

    @NotBlank(message = "Name is required")
    private String name;
}
