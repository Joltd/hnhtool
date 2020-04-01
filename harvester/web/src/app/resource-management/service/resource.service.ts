import {Injectable} from '@angular/core';
import {Resource} from "../model/resource";
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from "../../../environments/environment";
import {Observable} from 'rxjs';
import {ErrorHubService} from "../../core/common/error-hub/service/error-hub.service";
import {map} from 'rxjs/operators';

@Injectable()
export class ResourceService {

    constructor(private http: HttpClient, private errorHub: ErrorHubService) {}

    list(page: number, name: string, unknown: boolean): Observable<Resource[]> {
        let params = new HttpParams()
            .set('page', page?.toString())
            .set('name', name)
            .set('unknown', unknown?.toString())
        return this.http.get<any[]>(environment.apiUrl + '/resource', {params})
            .pipe(
                map(result => {
                    return result.map(entry => new Resource(entry.id, entry.name, entry.visual, entry.unknown));
                })
            )
    }

}
