package dravin.com.authentication.requestmodel.superAdmin;


import dravin.com.repository.constant.enumConstant.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ActivateRequestModel {

    @NotNull(message = "Id is required.")
    @Positive(message = "Id must be greater than 0.")
    private Long id;

    @NotNull(message = "Status is required.")
    private Status status;

}
