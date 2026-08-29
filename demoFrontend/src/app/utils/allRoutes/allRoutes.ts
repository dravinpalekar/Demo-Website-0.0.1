import { readonly } from "@angular/forms/signals";

export class allRoutes {

    // Define all the routes used in the application for frontend

    static readonly notFound = "notFound";
    static readonly login = "login";
    static readonly signUp = "signUp";
    static readonly logOut = "logout";
    static readonly refreshToken = "refreshToken";
    static readonly forgotPassword = "forgotPassword";
    static readonly superAdminLogin = "superAdminLogin";
    static readonly superAdminSignUp = "superAdminSignUp";
    static readonly superAdminDashboard = "superAdminDashboard";
    static readonly userDashboard = "dashboard";
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
    static readonly findFriend = "findFriend";
    static readonly friendLists = "friendList";
    static readonly friendRequestNotify = "friendRequestNotify";
    static readonly chatBox = "chat";


    // Define all the routes used in the application for frontend



    // Define all the routes used in the application for backend
    static readonly entryPointBackendBaseUrl = "http://localhost:8080/";
    static readonly userBackendBaseUrl = "http://localhost:8082/";
    static readonly backendWebSocketBaseUrl = "http://localhost:8081/";
    static readonly backendWebSocketUrl = "ws://localhost:8081/ws";

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
    static readonly people = "people/";
    static readonly friendList = "friendList/";
    static readonly friendRequestList = "friendRequestList/";
    static readonly send = "send/";
    static readonly accept = "accept/";
    static readonly cancel = "cancel/";
    static readonly request = "request";
    static readonly uploadFile = "upload/file";
    static readonly createRoom = "create/room";

    static readonly oneToOneSendMessageBackendUrl = "oneToOneSendMessage";
    static readonly oneToOneAddUserBackendUrl = "oneToOneAddUser";
    static readonly userPrivateBackendUrl = "/user/private";


    static readonly loginBackendUrl = this.entryPointBackendBaseUrl + this.api + this.auth + this.signIn;
    static readonly signUpBackendUrl = this.entryPointBackendBaseUrl + this.api + this.auth + this.signUp;

     static readonly logOutBackendUrl = this.entryPointBackendBaseUrl + this.api + this.auth + this.logOut;
     static readonly refreshTokenBackendUrl = this.entryPointBackendBaseUrl + this.api + this.auth + this.refreshToken;

    static readonly createRoleBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.role + this.create;
    static readonly getRoleBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.role + this.get;
    static readonly deleteRoleByIdBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.role + this.delete;
    static readonly updateRoleByIdBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.role + this.update;

    static readonly createPermissionBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.permission + this.create;
    static readonly updatePermissionByIdBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.permission + this.update;
    static readonly getPermissionBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.permission + this.get;
    static readonly deletePermissionBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.permission + this.delete;

    static readonly updateMyProfileSuperAdminBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.myProfile + this.create;

    static readonly getMyProfileSuperAdminBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.myProfile + this.get;

    static readonly getMyImageSuperAdminBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.myProfile + this.getMyImage;

    static readonly getUserBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.user + this.get;
    static readonly deleteUserBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.user + this.delete;
    static readonly activateDeactivateUserBackendUrl = this.entryPointBackendBaseUrl + this.api + this.super + this.user + this.activateDeactivate;
    // Define all the routes used in the application for backend









    // Define all normal user routes used in the appliation for backend

    static readonly getMyProfileBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.myProfile + this.get;
    static readonly getMyImageBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.myProfile + this.getMyImage;
    static readonly updateMyProfileBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.myProfile + this.create;
    static readonly getAllUserListBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.people + this.get;

    static readonly sendFriendRequestBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.send + this.request;
    static readonly acceptFriendRequestBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.accept + this.request;
    static readonly getFriendListBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.friendList + this.get;
    static readonly getFriendRequestListBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.friendRequestList + this.get;
    static readonly cancelFriendRequestBackendUrl = this.userBackendBaseUrl + this.api + this.user + this.cancel + this.request;
    // Define all normal user routes used in the appliation for backend










    // Define websocket routes used in the application for frontend

    static readonly oneToOneSendMessage = "/app/" + this.oneToOneSendMessageBackendUrl;
    static readonly oneToOneAddUser = "/app/" + this.oneToOneAddUserBackendUrl;
    static readonly uploadImageBackendUrl = this.backendWebSocketBaseUrl + this.api + this.user + this.uploadFile
    static readonly createRoomOrGetRoomBackendUrl = this.backendWebSocketBaseUrl + this.api + this.user + this.createRoom
    // Define websocket routes used in the application for frontend
}
