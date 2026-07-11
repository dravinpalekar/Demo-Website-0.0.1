export class createRoleModel{

    constructor(public roleName: string, public permissionsName: string){
        this.roleName = roleName;
        this.permissionsName = permissionsName;
    }
}