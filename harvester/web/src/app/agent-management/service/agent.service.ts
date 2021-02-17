import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Agent} from "../model/agent";
import {environment} from "../../../environments/environment";
import {plainToClass} from "class-transformer";
import {map} from "rxjs/operators";
import {sha256} from "js-sha256";


@Injectable()
export class AgentService {

    constructor(private http: HttpClient) {}

    list(): Observable<Agent[]> {
        return this.http.get<any[]>(environment.apiUrl + '/agent')
            .pipe(map(result => result.map(entry => plainToClass(Agent, entry))));
    }

    byId(id: number): Observable<Agent> {
        return this.http.get<any>(environment.apiUrl + '/agent/' + id)
            .pipe(map(result => plainToClass(Agent, result)));
    }

    update(agent: Agent): Observable<any> {
        let result = sha256(agent.password);
        return this.http.post(
            environment.apiUrl + '/agent',
            {
                id: agent.id,
                username: agent.username,
                password: result
            }
        );
    }

    updateEnabled(id: number, enabled: boolean): Observable<any> {
        let params = new HttpParams()
            .set('enabled', String(enabled))
        return this.http.post(environment.apiUrl + '/agent/' + id, null, {params});
    }

    updateAccident(id: number, accident: boolean): Observable<any> {
        let params = new HttpParams()
            .set('accident', String(accident))
        return this.http.post(environment.apiUrl + '/agent/' + id, null, {params});
    }

    listCharacter(id: number): Observable<string[]> {
        return this.http.get<string[]>(environment.apiUrl + '/agent/' + id + '/character');
    }

    updateCharacter(id: number, character: string): Observable<void> {
        return this.http.post<void>(environment.apiUrl + '/agent/' + id + '/character/' + character, null);
    }

}
