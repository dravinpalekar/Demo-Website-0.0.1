import { HttpClient } from '@angular/common/http';
import { inject, Service, signal } from '@angular/core';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { IdModel } from '../model/requestModel/IdModel';
import { map, of, switchMap, tap } from 'rxjs';

@Service()
export class UserService {

    private http = inject(HttpClient);

    private _friendList = signal<any[]>([]);
    public friendList = this._friendList.asReadonly();

    private friendListLoaded = false;

    constructor() {

    }


    public getAllUserList() {

        return this.http.get<any[]>(allRoutes.getAllUserListBackendUrl);
    }

    public sendFriendRequest(requestData: IdModel) {

        return this.http.post(allRoutes.sendFriendRequestBackendUrl, requestData);
    }

    public acceptFriendRequest(requestData: IdModel) {

        return this.http.post(allRoutes.acceptFriendRequestBackendUrl, requestData).pipe(
            switchMap(response =>
            this.refreshFriendList().pipe(
                map(() => response)
            )
        )
        );
    }

    public getFriendList() {

        // Already loaded hai to API call mat karo
        if (this.friendListLoaded) {
            return of(this._friendList());
        }

        return this.http.get<any[]>(allRoutes.getFriendListBackendUrl).pipe(tap(data => {
            this._friendList.set(JSON.parse(JSON.stringify(data)).data);
             this.friendListLoaded = true;
        }));
    }

   public refreshFriendList() {

        return this.http.get<any>(allRoutes.getFriendListBackendUrl).pipe(
            tap(response => {

                const data = response?.data ?? [];
                this._friendList.set(data);
                this.friendListLoaded = true;
            })
        );

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

    public createRoomOrGetRoom(requestData: IdModel){

        return this.http.post(allRoutes.createRoomOrGetRoomBackendUrl, requestData);
    }

}
