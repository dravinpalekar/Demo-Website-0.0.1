package dravin.com.authentication.requestmodel.superAdmin;


import dravin.com.repository.constant.enumConstant.Permissions;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreatePermissionRequestModel {

    @NotNull(message = "Permission name is required.")
    private Permissions name;

}
