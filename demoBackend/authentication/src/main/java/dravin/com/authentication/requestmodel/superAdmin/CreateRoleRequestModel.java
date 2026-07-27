package dravin.com.authentication.requestmodel.superAdmin;


import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.constant.enumConstant.Roles;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRoleRequestModel {


    @NotNull(message = "Role name is required")
    private Roles roleName;

    @NotNull(message = "Permission name is required")
    private Permissions permissionsName;
}
