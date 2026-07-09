export class LoginModel{

    // private email: String | undefined;
    // private password: String | undefined;

    constructor(public userName: string, public password: string){
        this.userName = userName;
        this.password = password;
    }
}