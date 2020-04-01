import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {ErrorHubService} from "./error-hub.service";
import {ErrorInfo} from "../model/ErrorInfo";
import {catchError} from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

    constructor(private errorHubService: ErrorHubService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let params = req.params;
        for (const key of params.keys()) {
            if (params.get(key) === undefined) {
                params = params.delete(key, undefined);
            }
        }
        req = req.clone({params});

        return next.handle(req)
            .pipe(catchError(error => {
                this.errorHubService.errors.push(new ErrorInfo(error));
                return throwError(error);
            }))
    }

}
