import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {ErrorHubService} from "./error-hub.service";
import {catchError, map} from 'rxjs/operators';

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
            .pipe(
                map((event: HttpEvent<any>) => {
                    if (event instanceof HttpResponse) {
                        let response = event.body;
                        if (response.success) {
                            return event.clone({body: response.value});
                        } else {
                            throw new Error(response.error);
                        }
                    }
                }),
                catchError(error => {
                    this.errorHubService.registerError(error);
                    return throwError(error);
                })
            );
    }

}
