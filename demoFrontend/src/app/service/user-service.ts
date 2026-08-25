import { HttpClient, HttpParams } from '@angular/common/http';
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

    // Cache Map: search term + page number ke base par data store karega
    private pageCache = new Map<string, any[]>();

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

    // --- Backend Paginated & Cached Method ---
    public getFriendListPaginated(page: number, size: number, search: string = '') {
        const cacheKey = `${search.trim().toLowerCase()}_page_${page}_size_${size}`;

        // 1. Agar ye specific page cache me hai to direct update karke return karein
        if (this.pageCache.has(cacheKey)) {
            const cachedRecords = this.pageCache.get(cacheKey)!;
            this.updateFriendListSignal(cachedRecords, page === 0);
            return of({ data: cachedRecords, fromCache: true });
        }

        // 2. Agar cache me nahi hai to API call karein
        let params = new HttpParams().set('page', page.toString()).set('size', size.toString());

        if (search.trim()) {
            params = params.set('search', search.trim());
        }

        return this.http.get<any>(allRoutes.getFriendListBackendUrl, { params }).pipe(
            tap(response => {
                const data = response?.data ?? [];
                
                // Cache me store karein
                this.pageCache.set(cacheKey, data);
                
                // Signal update karein (page 1 par reset, page 2+ par append)
                this.updateFriendListSignal(data, page === 0);
                this.friendListLoaded = true;
            })
        );
    }

    private updateFriendListSignal(newData: any[], isFirstPage: boolean) {
        if (isFirstPage) {
            this._friendList.set(newData);
        } else {
            this._friendList.update(currentList => [...currentList, ...newData]);
        }
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

        // Cache invalidate karein jab nayi friend request accept ho
        this.pageCache.clear();

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
