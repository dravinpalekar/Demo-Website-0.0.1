export class signUpModel{

    // private email: String | undefined;
    // private password: String | undefined;

    constructor(public email: string, public password: string, public roles?: string[]){
        this.email = email;
        this.password = password;
         this.roles = roles;
    }
}