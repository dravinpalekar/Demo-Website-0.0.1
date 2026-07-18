export class allRoutes{

    // Define all the routes used in the application for frontend 

    static readonly notFound = "notFound";
    static readonly login = "login";
    static readonly signUp = "signUp";
    static readonly forgotPassword = "forgotPassword";
    static readonly superAdminLogin = "superAdminLogin";
    static readonly superAdminSignUp = "superAdminSignUp";
    static readonly superAdminDashboard = "superAdminDashboard";

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

    static readonly loginBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signIn;
    static readonly signUpBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signUp;
    static readonly createRoleBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.create;
    static readonly getRoleBackendUrl = this.backendBaseUrl + this.api + this.super + this.role + this.get;
    static readonly createPermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.create;
    static readonly getPermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.get;
    static readonly deletePermissionBackendUrl = this.backendBaseUrl + this.api + this.super + this.permission + this.delete;
    static readonly updateMyProfileBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.create;
    static readonly getMyProfileBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.get;
    static readonly getMyImageBackendUrl = this.backendBaseUrl + this.api + this.super + this.myProfile + this.getMyImage;
    // Define all the routes used in the application for backend
}