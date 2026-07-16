export class UpdateMyProfileModel{

    constructor(public firstName: string, public middleName: string,public lastName: string, public gender: string,public age: number, public country: string, public city:string, public pinCode:number, public address:string, public file?:File){
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.country = country;
        this.city = city;
        this.pinCode = pinCode;
        this.address = address;
        this.file = file;
    }
}