import { HttpEvent, HttpHandler, HttpInterceptor, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationAuthorizationService } from '../../service/authentication-authorization-service';

// export const jwtInterceptorInterceptor: HttpInterceptorFn = (req, next) => {
//   return next(req);
// };


export class JwtInterceptor implements HttpInterceptor {
    constructor(private authenticationService: AuthenticationAuthorizationService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add authorization header with jwt token if available
        let currentUser = this.authenticationService.currentUserValue;
        if (currentUser && currentUser.token) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${currentUser.token}`
                }
            });
        }

        return next.handle(request);
    }
}
