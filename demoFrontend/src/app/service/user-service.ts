import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { NameModel } from '../model/requestModel/NameModel';

@Service()
export class UserService {

    private http = inject(HttpClient);

    constructor() {
      
    }


    public getAllUserList() {

        return this.http.get<any[]>(allRoutes.getAllUserListBackendUrl);
    }

    public sendFriendRequest(requestData:NameModel){

        return this.http.post(allRoutes.sendFriendRequestBackendUrl,requestData);
    }

    public acceptFriendRequest(requestData:NameModel){

        return this.http.post(allRoutes.acceptFriendRequestBackendUrl,requestData);
    }


}
