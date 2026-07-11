import { inject, Injectable, PLATFORM_ID, Service } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { LoginModel } from '../model/requestModel/LoginModel';
import { allRoutes } from '../utils/allRoutes/allRoutes';
import { signUpModel } from '../model/requestModel/signUpModel';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

export enum Role {
    User = 'ROLE_USER',
    Admin = 'ROLE_USER',
    SuperAdmin = 'ROLE_SUPER_ADMIN',
}

export class UserValidateModel {
    roles: Role | undefined;
    Subject: string | undefined;
    token?: string;
}

export interface JwtPayload {
    // iss?: string;
    sub?: string;
    // aud?: string[] | string;
    exp?: number;
    // nbf?: number;
    iat?: number;
    // jti?: string;
    roles?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthenticationAuthorizationService {

    isBrowser: boolean = false;

    private http = inject(HttpClient);
    private platformId = inject(PLATFORM_ID);

    private currentUserSubject: BehaviorSubject<UserValidateModel>;
    public currentUser: Observable<UserValidateModel>;
    private roles: string[] = [];
    // private isAuthenticated = false;

    constructor(private router: Router) {

        this.isBrowser = isPlatformBrowser(this.platformId);

        let storedUser: UserValidateModel = new UserValidateModel();

        if (isPlatformBrowser(this.platformId)) {
            const sessionData = sessionStorage.getItem('currentUser');
            if (sessionData) {
                storedUser = JSON.parse(sessionData);
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
        return this.http.post<any>(allRoutes.loginBackendUrl, loginObject)
            .pipe(map(userValidateModel => {
                // login successful if there's a jwt token in the response
                if (userValidateModel && userValidateModel.token) {

                    var decoded: JwtPayload = jwtDecode(userValidateModel.token);

                    userValidateModel.Subject = decoded.sub;

                    JSON.parse(JSON.stringify([decoded.roles][0])).forEach((element: any) => {
                        this.roles.push(element.authority);
                    });

                    userValidateModel.roles = this.roles;
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    sessionStorage.setItem('currentUser', JSON.stringify(userValidateModel));
                    // sessionStorage.setItem('loggedIn', 'true');
                    this.currentUserSubject.next(userValidateModel);
                }
                return userValidateModel;
            }));
    }

    public get currentUserValue(): UserValidateModel {
        return this.currentUserSubject.value;
    }

    logout() {
        // remove user from local storage to log user out
        sessionStorage.removeItem('currentUser');
        // sessionStorage.removeItem('loggedIn');
        this.currentUserSubject.next(null!);
    }

}
