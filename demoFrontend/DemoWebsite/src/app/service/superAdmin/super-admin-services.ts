import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable, Service } from '@angular/core';
import { allRoutes } from '../../utils/allRoutes/allRoutes';
import { AuthenticationAuthorizationService } from '../authentication-authorization-service';
import { createNameModel } from '../../model/requestModel/superAdmin/createNameModel';
import { createRoleModel } from '../../model/requestModel/superAdmin/createRoleModel';
import { UpdateMyProfileModel } from '../../model/requestModel/superAdmin/UpdateMyProfileModel';

@Injectable({
  providedIn: 'root'
})
export class SuperAdminServices {

    private jwtToken?: string;

    private headers: HttpHeaders;

    constructor(private http: HttpClient, private authenticationServiceObject: AuthenticationAuthorizationService) {
        this.jwtToken = this.authenticationServiceObject.currentUserValue.token;
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

    public updateMyProfile(updateMyProfileModelObject: UpdateMyProfileModel) {
        const formData = new FormData();
        formData.append('firstName', updateMyProfileModelObject.firstName);
        formData.append('middleName', updateMyProfileModelObject.middleName);
        formData.append('lastName', updateMyProfileModelObject.lastName);
        formData.append('gender', updateMyProfileModelObject.gender);
        formData.append('country', updateMyProfileModelObject.country);
        formData.append('city', updateMyProfileModelObject.city);
        formData.append('age', updateMyProfileModelObject.age.toString());
        formData.append('pinCode', updateMyProfileModelObject.pinCode.toString());
        formData.append('address', updateMyProfileModelObject.address);
        formData.append('file', updateMyProfileModelObject.file!);
        // const headers: HttpHeaders = new HttpHeaders({ 'Authorization': `Bearer ${this.jwtToken}`, 'Content-Type': 'multipart/form-data'  });
        return this.http.post(allRoutes.updateMyProfileBackendUrl, formData, { headers: this.headers });
    }

    public getMyProfile() {

        return this.http.get<any[]>(allRoutes.getMyProfileBackendUrl, { headers: this.headers });
    }

    public getMyImage() {

        return this.http.get<any[]>(allRoutes.getMyImageBackendUrl, { headers: this.headers });
    }
}
