export class allRoutes{

    // Define all the routes used in the application for frontend 

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
    static readonly create = "create";


    static readonly loginBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signIn;
    static readonly signUpBackendUrl = this.backendBaseUrl + this.api + this.auth + this.signUp;

    // Define all the routes used in the application for backend
}