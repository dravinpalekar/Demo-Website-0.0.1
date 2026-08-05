import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Service, signal } from '@angular/core';
import { AuthenticationService, Role } from '../authentication-service';
import { allRoutes } from '../../utils/allRoutes/allRoutes';
import { createRoleModel } from '../../model/requestModel/superAdmin/createRoleModel';
import { createNameModel } from '../../model/requestModel/superAdmin/createNameModel';
import { UpdateMyProfileModel } from '../../model/requestModel/superAdmin/UpdateMyProfileModel';
import { Observable, of, tap } from 'rxjs';

@Service()
export class SuperAdminService {

    private jwtToken?: string;
    private roleName: any;
    private headers: HttpHeaders;
    private http = inject(HttpClient);
    private authenticationServiceObject = inject(AuthenticationService);

    //Define variable for storing cache data
    private cachedData = signal<any>(null);

    constructor() {
        this.jwtToken = this.authenticationServiceObject.currentUserValue?.token;
        this.roleName = this.authenticationServiceObject.currentUserValue?.roles;
        this.headers = new HttpHeaders({ 'Authorization': `Bearer ${this.jwtToken}`, 'Content-Type': 'application/json' });
    }

    public createRole(createRoleModelObject: createRoleModel) {

        return this.http.post(allRoutes.createRoleBackendUrl, createRoleModelObject, { headers: this.headers });
    }

    public updateRoleById(id: number, createRoleModelObject: createRoleModel) {

        return this.http.put(allRoutes.updateRoleByIdBackendUrl + "/" + id, createRoleModelObject, { headers: this.headers });
    }

    public getRoles() {

        return this.http.get<any[]>(allRoutes.getRoleBackendUrl, { headers: this.headers });
    }

    public getRoleById(id: number) {

        return this.http.get<any[]>(allRoutes.getRoleBackendUrl + "/" + id, { headers: this.headers });
    }

    public deleteRole(id: number) {

        return this.http.delete(allRoutes.deleteRoleByIdBackendUrl + "/" + id, { headers: this.headers });
    }

    public createPermission(createPermissionModelObject: createNameModel) {

        return this.http.post(allRoutes.createPermissionBackendUrl, createPermissionModelObject, { headers: this.headers });
    }

    public updatePermissionById(id: number, createPermissionModelObject: createNameModel) {

        return this.http.put(allRoutes.updatePermissionByIdBackendUrl + "/" + id, createPermissionModelObject, { headers: this.headers });
    }

    public getPermissions() {

        return this.http.get<any[]>(allRoutes.getPermissionBackendUrl, { headers: this.headers });
    }

    public getPermissionById(id: number) {

        return this.http.get<any[]>(allRoutes.getPermissionBackendUrl + "/" + id, { headers: this.headers });
    }

    public deletePermission(id: number) {

        return this.http.delete(allRoutes.deletePermissionBackendUrl + "/" + id, { headers: this.headers });
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
            "firstName": updateMyProfileModelObject.firstName, "middleName": updateMyProfileModelObject.middleName, "lastName": updateMyProfileModelObject.lastName,
            "gender": updateMyProfileModelObject.gender, "country": updateMyProfileModelObject.country, "city": updateMyProfileModelObject.city,
            "age": updateMyProfileModelObject.age, "pinCode": updateMyProfileModelObject.pinCode, "address": updateMyProfileModelObject.address,
        };

        formDataTemp.append('updateMyProfileRequest', new Blob([JSON.stringify(profileData)], { type: 'application/json' }));
        if (updateMyProfileModelObject.file && updateMyProfileModelObject.file.size > 0) {
            formDataTemp.append('file', updateMyProfileModelObject.file);
            this.clearCache();
        }

        // this.headers.delete('Content-Type');
        const headers = this.headers.delete('Content-Type');
        if (this.roleName[0] == Role.User) {
             return this.http.post(allRoutes.updateMyProfileBackendUrl, formDataTemp, { headers: headers });
        }
        else
        {
            return this.http.post(allRoutes.updateMyProfileSuperAdminBackendUrl, formDataTemp, { headers: headers });
        }
        
    }

    public getMyProfile() {

        if (this.roleName[0] == Role.User) {
            return this.http.get<any[]>(allRoutes.getMyProfileBackendUrl, { headers: this.headers });
        }
        else {
            return this.http.get<any[]>(allRoutes.getMyProfileSuperAdminBackendUrl, { headers: this.headers });
        }

    }

    public getMyImage() {

        if (this.cachedData()) {
            // Agar data pehle se cache me hai, to bina API call kiye wahi return kar do
            return of(this.cachedData());
        }
        // Agar cache khali hai, to API call karo aur tap() ke jariye use cache me save kar lo
        if (this.roleName?.includes(Role.User)) {
            return this.http.get<any>(allRoutes.getMyImageBackendUrl, { headers: this.headers }).pipe(tap(data => this.cachedData.set(data)));
        }
        else {
            return this.http.get<any>(allRoutes.getMyImageSuperAdminBackendUrl, { headers: this.headers }).pipe(tap(data => this.cachedData.set(data)));
        }

    }

    clearCache(): void {
        this.cachedData.set(null);
    }

    public getUsers() {
        return this.http.get<any[]>(allRoutes.getUserBackendUrl, { headers: this.headers });
    }

    public deleteUserById(id: number) {

        return this.http.delete(allRoutes.deleteUserBackendUrl + "/" + id, { headers: this.headers });
    }

    public activateDeactivate(requestData: any) {

        return this.http.post(allRoutes.activateDeactivateUserBackendUrl, requestData, { headers: this.headers });
    }

}
