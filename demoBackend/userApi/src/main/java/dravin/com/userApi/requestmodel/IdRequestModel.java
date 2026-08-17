package dravin.com.userApi.requestmodel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class IdRequestModel {

    @NotNull(message = "Id is required")
    @Positive(message = "Id must be greater than 0")
    private Long id;
}
