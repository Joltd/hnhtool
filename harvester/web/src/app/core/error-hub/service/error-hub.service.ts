import {Injectable} from '@angular/core';
import {ErrorInfo} from "../model/ErrorInfo";

@Injectable({providedIn: 'root'})
export class ErrorHubService {

    errors: ErrorInfo[] = [];

}
