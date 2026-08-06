import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, PLATFORM_ID, Service } from '@angular/core';
import { AuthenticationService } from './authentication-service';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { isPlatformBrowser } from '@angular/common';

@Service()
export class UserService {


    // private platformId = inject(PLATFORM_ID);

    // private headers: HttpHeaders = new HttpHeaders;
    private http = inject(HttpClient);
    // private authenticationServiceObject = inject(AuthenticationService);

    constructor() {
      
    }

     ngOnInit(): void {
        //  if (isPlatformBrowser(this.platformId)) {

        // }
     }

    



    public getAllUserList() {

        return this.http.get<any[]>(allRoutes.getAllUserListBackendUrl);
    }


}
