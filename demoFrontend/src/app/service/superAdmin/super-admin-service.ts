import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service, signal } from '@angular/core';
import { Role } from '../authentication-service';
import { allRoutes } from '../../utils/allRoutes/allRoutes';
import { createRoleModel } from '../../model/requestModel/superAdmin/createRoleModel';
import { createNameModel } from '../../model/requestModel/superAdmin/createNameModel';
import { UpdateMyProfileModel } from '../../model/requestModel/superAdmin/UpdateMyProfileModel';
import { of, tap } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Service()
export class SuperAdminService {

    private cookieService = inject(CookieService);
    private roleName: any;
    private http = inject(HttpClient);

    //Define variable for storing cache data
    private cachedData = signal<any>(null);

    constructor() {

        if (this.cookieService.get("isLoggedIn")) {
            this.roleName = JSON.parse(this.cookieService.get("userSession")).roles;
        }
    }

    public createRole(createRoleModelObject: createRoleModel) {

        return this.http.post(allRoutes.createRoleBackendUrl, createRoleModelObject);
    }

    public updateRoleById(id: number, createRoleModelObject: createRoleModel) {

        return this.http.put(allRoutes.updateRoleByIdBackendUrl + "/" + id, createRoleModelObject);
    }

    public getRoles(page: number = 0, size: number = 10, filterColumnName: string = '', searchItem: string = '', sort: string = 'id,DESC') {

      let params = new HttpParams().set('page', page.toString()).set('size', size.toString()).set('sort', sort);

      if(filterColumnName != null && filterColumnName.trim() != "")
      {
        params = params.set('ColumnName', filterColumnName).set('searchItem', searchItem);
      }

      return this.http.get<any>(allRoutes.getRoleBackendUrl, { params });
    }

    public getRoleById(id: number) {

        return this.http.get<any[]>(allRoutes.getRoleBackendUrl + "/" + id);
    }

    public deleteRole(id: number) {

        return this.http.delete(allRoutes.deleteRoleByIdBackendUrl + "/" + id);
    }

    public createPermission(createPermissionModelObject: createNameModel) {

        return this.http.post(allRoutes.createPermissionBackendUrl, createPermissionModelObject);
    }

    public updatePermissionById(id: number, createPermissionModelObject: createNameModel) {

        return this.http.put(allRoutes.updatePermissionByIdBackendUrl + "/" + id, createPermissionModelObject);
    }

    public getPermissions(page: number = 0, size: number = 10, searchItem: string = '', sort: string = 'id,DESC') {

      let params = new HttpParams().set('page', page.toString()).set('size', size.toString()).set('sort', sort);

      if(searchItem != null && searchItem.trim() != "")
      {
        params = params.set('searchItem', searchItem);
      }

      return this.http.get<any>(allRoutes.getPermissionBackendUrl, { params });
    }

    public getPermissionById(id: number) {

        return this.http.get<any[]>(allRoutes.getPermissionBackendUrl + "/" + id);
    }

    public deletePermission(id: number) {

        return this.http.delete(allRoutes.deletePermissionBackendUrl + "/" + id);
    }

    public updateMyProfile(updateMyProfileModelObject: UpdateMyProfileModel) {

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

        if (this.roleName[0] == Role.User) {
            return this.http.post(allRoutes.updateMyProfileBackendUrl, formDataTemp);
        }
        else {
            return this.http.post(allRoutes.updateMyProfileSuperAdminBackendUrl, formDataTemp);
        }
    }

    public getMyProfile() {

        if (this.roleName[0] == Role.User) {
            return this.http.get<any[]>(allRoutes.getMyProfileBackendUrl);
        }
        else {
            return this.http.get<any[]>(allRoutes.getMyProfileSuperAdminBackendUrl);
        }
    }

    public getMyImage() {

        if (this.cachedData()) {
            // Agar data pehle se cache me hai, to bina API call kiye wahi return kar do
            return of(this.cachedData());
        }
        // Agar cache khali hai, to API call karo aur tap() ke jariye use cache me save kar lo
        if (this.roleName?.includes(Role.User)) {
            return this.http.get<any>(allRoutes.getMyImageBackendUrl).pipe(tap(data => this.cachedData.set(data)));
        }
        else {
            return this.http.get<any>(allRoutes.getMyImageSuperAdminBackendUrl).pipe(tap(data => this.cachedData.set(data)));
        }
    }

    clearCache(): void {
        this.cachedData.set(null);
    }

    public getUsers(page: number = 0, size: number = 10, filterColumnName: string, searchItem: string, sort: string = 'id,DESC') {

      let params = new HttpParams().set('page', page.toString()).set('size', size.toString()).set('sort', sort);

      if(filterColumnName != null && filterColumnName.trim() != "")
      {
        params = params.set('ColumnName',filterColumnName).set('searchItem',searchItem);
      }

      return this.http.get<any[]>(allRoutes.getUserBackendUrl, { params });
    }

    public deleteUserById(id: number) {

        return this.http.delete(allRoutes.deleteUserBackendUrl + "/" + id);
    }

    public activateDeactivate(requestData: any) {

        return this.http.post(allRoutes.activateDeactivateUserBackendUrl, requestData);
    }

}
