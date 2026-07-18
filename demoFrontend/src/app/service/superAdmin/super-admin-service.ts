import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { AuthenticationService } from '../authentication-service';
import { allRoutes } from '../../utils/allRoutes/allRoutes';
import { createRoleModel } from '../../model/requestModel/superAdmin/createRoleModel';
import { createNameModel } from '../../model/requestModel/superAdmin/createNameModel';
import { UpdateMyProfileModel } from '../../model/requestModel/superAdmin/UpdateMyProfileModel';

@Service()
export class SuperAdminService {

    private jwtToken?: string;
    private headers: HttpHeaders;
    private http = inject(HttpClient);
    private authenticationServiceObject = inject(AuthenticationService);

    constructor() {
        this.jwtToken = this.authenticationServiceObject.currentUserValue?.token;
        this.headers = new HttpHeaders({ 'Authorization': `Bearer ${this.jwtToken}`, 'Content-Type': 'application/json' });
    }

    public createRole(createRoleModelObject: createRoleModel) {

        return this.http.post(allRoutes.createRoleBackendUrl, createRoleModelObject, { headers: this.headers });
    }

    public getRoles() {

        return this.http.get<any[]>(allRoutes.getRoleBackendUrl, { headers: this.headers });
    }

    public createPermission(createPermissionModelObject: createNameModel) {

        return this.http.post(allRoutes.createPermissionBackendUrl, createPermissionModelObject, { headers: this.headers });
    }

    public getPermissions() {

        return this.http.get<any[]>(allRoutes.getPermissionBackendUrl, { headers: this.headers });
    }

    public deletePermission(id:number){
     
        return this.http.delete(allRoutes.deletePermissionBackendUrl + "/" +id, { headers: this.headers });
    }

    public updateMyProfile(updateMyProfileModelObject: UpdateMyProfileModel) {
        // const formData = new FormData();
        // formData.append('firstName', updateMyProfileModelObject.firstName);
        // formData.append('middleName', updateMyProfileModelObject.middleName);
        // formData.append('lastName', updateMyProfileModelObject.lastName);
        // formData.append('gender', updateMyProfileModelObject.gender);
        // formData.append('country', updateMyProfileModelObject.country);
        // formData.append('city', updateMyProfileModelObject.city);
        // formData.append('age', updateMyProfileModelObject.age.toString());
        // formData.append('pinCode', updateMyProfileModelObject.pinCode.toString());
        // formData.append('address', updateMyProfileModelObject.address);
        // formData.append('file', updateMyProfileModelObject.file!);
        // if (updateMyProfileModelObject.file) {
        //     formData.append('file', updateMyProfileModelObject.file);
        // }
        const formDataTemp = new FormData();
        const profileData = {
            "firstName":updateMyProfileModelObject.firstName, "middleName":updateMyProfileModelObject.middleName, "lastName":updateMyProfileModelObject.lastName,
            "gender":updateMyProfileModelObject.gender, "country":updateMyProfileModelObject.country, "city":updateMyProfileModelObject.city,
            "age":updateMyProfileModelObject.age, "pinCode":updateMyProfileModelObject.pinCode, "address":updateMyProfileModelObject.address,
        };
        
        formDataTemp.append('updateMyProfileRequest', new Blob([JSON.stringify(profileData)],{ type: 'application/json' }));
        formDataTemp.append('file', updateMyProfileModelObject.file!);

        // this.headers.delete('Content-Type');
        const headers = this.headers.delete('Content-Type');
        return this.http.post(allRoutes.updateMyProfileBackendUrl, formDataTemp, { headers: headers });
    }

    public getMyProfile() {

        return this.http.get<any[]>(allRoutes.getMyProfileBackendUrl, { headers: this.headers });
    }

    public getMyImage() {

        return this.http.get<any[]>(allRoutes.getMyImageBackendUrl, { headers: this.headers });
    }

}
