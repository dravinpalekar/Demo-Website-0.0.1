import { inject, PLATFORM_ID, Service } from '@angular/core';
import { LoginModel } from '../model/requestModel/LoginModel';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { signUpModel } from '../model/requestModel/signUpModel';
import { CookieService } from 'ngx-cookie-service';

export enum Role {
    User = 'ROLE_USER',
    Admin = 'ROLE_ADMIN',
    SuperAdmin = 'ROLE_SUPER_ADMIN',
    Guest = "ROLE_GUEST"
}

export class UserValidateModel {
    roles: Role[] | undefined;
    userName: string | undefined;
}


@Service()
export class AuthenticationService {

    isBrowser: boolean = false;
    private http = inject(HttpClient);
    private platformId = inject(PLATFORM_ID);

    private currentUserSubject: BehaviorSubject<UserValidateModel>;
    public currentUser: Observable<UserValidateModel>;
    private cookieService = inject(CookieService);
    private roles: Role[] = [];

    constructor() {
        this.isBrowser = isPlatformBrowser(this.platformId);

        let storedUser: UserValidateModel = new UserValidateModel();

        if (isPlatformBrowser(this.platformId)) {
            if (this.cookieService.get("isLoggedIn")) {
                storedUser = JSON.parse(this.cookieService.get("userSession"));
            }
        }

        this.currentUserSubject = new BehaviorSubject<UserValidateModel>(storedUser);
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public signUpUser(signUpObject: signUpModel) {
        return this.http.post(allRoutes.signUpBackendUrl, signUpObject);
        // .pipe(retry(1), catchError(this.handleError));
    }


    public loginUser(loginObject: LoginModel) {
        return this.http.post<any>(allRoutes.loginBackendUrl, loginObject, { withCredentials: true })
            .pipe(map(userValidateModel => {

                // login successful if there's a data in the response
                if (userValidateModel && userValidateModel.data) {

                    let responseData = JSON.parse(JSON.stringify(userValidateModel));

                    responseData.data.roles.forEach((element: any) => {
                        this.roles.push(element);
                    });

                    let setUserData: UserValidateModel = new UserValidateModel();
                    setUserData.roles = this.roles;
                    setUserData.userName = responseData.data.userName;

                    this.cookieService.set("userSession", JSON.stringify(setUserData), 86400000 / 1000, '/', '', true, 'Strict');
                    this.cookieService.set("isLoggedIn", "true", 86400000 / 1000, '/', '', true, 'Strict');

                    this.currentUserSubject.next(setUserData);
                }
                return userValidateModel;
            }
            ));
    }

    logout() {
        // remove user from cookie storage to log user out
        this.cookieService.delete('cookieToken', '/');
        this.cookieService.delete('userSession', '/');
        this.cookieService.delete('isLoggedIn', '/');
        this.currentUserSubject.next(null!);
    }
}
