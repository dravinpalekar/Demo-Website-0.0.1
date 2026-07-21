export class allRoutes {

    // Define all the routes used in the application for frontend 

    static readonly notFound = "notFound";
    static readonly login = "login";
    static readonly signUp = "signUp";
    static readonly forgotPassword = "forgotPassword";
    static readonly superAdminLogin = "superAdminLogin";
    static readonly superAdminSignUp = "superAdminSignUp";
    static readonly superAdminDashboard = "superAdminDashboard";
    static readonly createRole = "createRole";
    static readonly createPermission = "createPermission";
    static readonly managePermission = "managePermission";
    static readonly manageRole = "manageRole";
    static readonly manageProfile = "myProfile";
    static readonly manageUser = "manageUser";
    static readonly manageRoles = this.superAdminDashboard + "/" + this.manageRole;
    static readonly managePermissions = this.superAdminDashboard + "/" + this.managePermission;
    static readonly editPermission = this.superAdminDashboard + "/editPermission/";
    static readonly editRole = this.superAdminDashboard + "/editRole/";


    // Define all the routes used in the application for frontend 



    // Define all the routes used in the application for backend 
    static readonly backendBaseUrl = "http://localhost:8080/";
    static readonly api = "api/";
    static readonly auth = "auth/";
    static readonly signIn = "signIn";
    static readonly super = "super/admin/";
    static readonly role = "role/";
    static readonly myProfile = "myProfile/";
    static readonly permission = "permission/";
    static readonly get = "get";
    static readonly delete = "delete";
    static readonly update = "update";
    static readonly create = "create";
    static readonly getMyImage = "getMyImage";
    static readonly user = "user/";
    static readonly activateDeactivate = "activateDeactivate";

    static readonly loginBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signIn;
    static readonly signUpBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signUp;

    static readonly createRoleBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.create;
    static readonly getRoleBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.get;
    static readonly deleteRoleByIdBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.delete;
    static readonly updateRoleByIdBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.update;

    static readonly createPermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.create;
    static readonly updatePermissionByIdBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.update;
    static readonly getPermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.get;
    static readonly deletePermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.delete;

    static readonly updateMyProfileBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.create;
    static readonly getMyProfileBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.get;
    static readonly getMyImageBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.getMyImage;

    static readonly getUserBackendUrl = this.backendBaseUrl + this.api + this.super + this.user + this.get;
    static readonly deleteUserBackendUrl = this.backendBaseUrl + this.api + this.super + this.user + this.delete;
    static readonly activateDeactivateUserBackendUrl = this.backendBaseUrl + this.api + this.super + this.user + this.activateDeactivate;
    // Define all the routes used in the application for backend
}