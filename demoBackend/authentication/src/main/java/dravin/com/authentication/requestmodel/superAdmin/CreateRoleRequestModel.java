package dravin.com.authentication.requestmodel.superAdmin;


import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.constant.enumConstant.Roles;
import lombok.Getter;

@Getter
public class CreateRoleRequestModel {

    private Roles roleName;

    private Permissions permissionsName;
}
