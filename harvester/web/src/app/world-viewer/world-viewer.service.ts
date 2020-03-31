import {environment} from "../../environments/environment";
import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Space} from './model/space';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Point} from "./model/point";
import {KnownObject} from "./model/known-object";
import {ErrorHubService} from "../core/service/error-hub.service";

@Injectable({
  providedIn: 'root'
})
export class WorldViewerService {

  constructor(private http: HttpClient, private errorHubService: ErrorHubService) {}

  public loadSpaces(): Observable<Space[]> {
    return this.http.get<Space[]>(environment.apiUrl + '/space').pipe(this.errorHubService.catchError());
  }

  public loadKnownObjects(spaceId: number, from: Point, to: Point): Observable<KnownObject[]> {
    let params = new HttpParams()
        .set("spaceId", spaceId.toString())
        .set("fromX", from.x.toString())
        .set("fromY", from.y.toString())
        .set("toX", to.x.toString())
        .set("toY", to.y.toString());
    return this.http.get<any[]>(environment.apiUrl + '/knownObject', {params})
        .pipe(
            this.errorHubService.catchError(),
            map((result: any[]) => result.map(entry => new KnownObject(entry)))
        );
  }

}
