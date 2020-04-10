import {Injectable} from '@angular/core';
import {Resource} from "../model/resource";
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from "../../../environments/environment";
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {plainToClass} from "class-transformer";
import {ResourceGroup} from "../model/resource-group";

@Injectable()
export class ResourceService {

    constructor(private http: HttpClient) {}

    list(page: number = 0, size: number = 10, name: string, unknown: boolean): Observable<Resource[]> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('name', name)
            .set('unknown', unknown?.toString())
        return this.http.get<any[]>(environment.apiUrl + '/resource', {params})
            .pipe(map(result => result.map(entry => plainToClass(Resource, entry))))
    }

    byId(id: number): Observable<Resource> {
        return this.http.get<any>(environment.apiUrl + '/resource/' + id)
            .pipe(map(result => plainToClass(Resource, result)));
    }

    update(resource: Resource): Observable<any> {
        return this.http.post(environment.apiUrl + '/resource', resource);
    }

    listGroups(page: number = 0, size: number = 10, name: string): Observable<ResourceGroup[]> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('name', name);
        return this.http.get<any[]>(environment.apiUrl + '/resource/group', {params})
            .pipe(map(result => result.map(entry => plainToClass(ResourceGroup, entry))));
    }

    updateGroup(resource: number, group: number): Observable<any> {
        let params = new HttpParams()
            .set('resource', resource.toString())
            .set('group', group?.toString())
        return this.http.post(environment.apiUrl + '/resource/group', null, {params});
    }

}
