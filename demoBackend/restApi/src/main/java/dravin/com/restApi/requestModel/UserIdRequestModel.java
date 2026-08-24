package dravin.com.restApi.requestModel;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UserIdRequestModel {

    @NotNull(message = "Id is required")
    @Positive(message = "Id must be greater than 0")
    private Long id;
}
