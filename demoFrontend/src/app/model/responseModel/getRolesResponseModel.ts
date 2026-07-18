export class getRolesResponseModel{

    public id: number | undefined;
    public name: String | undefined;
    public createdAt: String | undefined;

    constructor(id:number, name: string, createdAt: string){
        this.id = this.id;
        this.name = name;
        this.createdAt = createdAt;
    }
}