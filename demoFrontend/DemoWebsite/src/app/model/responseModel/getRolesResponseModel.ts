export class getRolesResponseModel{

    private name: String | undefined;
    private createdAt: String | undefined;

    constructor(name: string, createdAt: string){
        this.name = name;
        this.createdAt = createdAt;
    }
}