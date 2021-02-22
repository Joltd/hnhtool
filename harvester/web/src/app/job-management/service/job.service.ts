import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Page} from "../../core/page";
import {Job} from "../model/job";
import {environment} from "../../../environments/environment";
import {map} from "rxjs/operators";
import {plainToClass} from "class-transformer";

@Injectable()
export class JobService {

    constructor(private http: HttpClient) {}

    list(page: number, size: number): Observable<Page<Job>> {
        let params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size));
        return this.http.get<any>(environment.apiUrl + '/job', {params})
            .pipe(
                map(result => {
                    return {
                        data: result.data.map(entry => plainToClass(Job, entry)),
                        total: result.total
                    }
                })
            );
    }

}