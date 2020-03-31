import {Injectable} from '@angular/core';
import {Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ErrorInfo} from "../model/ErrorInfo";

@Injectable({providedIn: 'root'})
export class ErrorHubService {

    errors: ErrorInfo[];

    catchError<T, O extends Observable<T>>(): OperatorFunction<T, T> {
        return catchError(error => {
            this.errors.push(new ErrorInfo(error.message));
            return throwError(error);
        });
    }

}
