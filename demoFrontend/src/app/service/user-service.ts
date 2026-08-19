import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { NameModel } from '../model/requestModel/NameModel';
import { IdModel } from '../model/requestModel/IdModel';

@Service()
export class UserService {

    private http = inject(HttpClient);

    constructor() {

    }


    public getAllUserList() {

        return this.http.get<any[]>(allRoutes.getAllUserListBackendUrl);
    }

    public sendFriendRequest(requestData: IdModel) {

        return this.http.post(allRoutes.sendFriendRequestBackendUrl, requestData);
    }

    public acceptFriendRequest(requestData: IdModel) {

        return this.http.post(allRoutes.acceptFriendRequestBackendUrl, requestData);
    }

    public getFriendList() {

        return this.http.get<any[]>(allRoutes.getFriendListBackendUrl);
    }

    public getFriendRequestList() {

        return this.http.get<any[]>(allRoutes.getFriendRequestListBackendUrl);
    }

     public cancelFriendRequest(requestData: IdModel) {

        return this.http.post(allRoutes.cancelFriendRequestBackendUrl, requestData);
    }

    public uploadChatImage(file:FormData){

        return this.http.post(allRoutes.uploadImageBackendUrl, file);
    }


}
