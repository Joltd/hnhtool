import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Agent} from "../model/agent";
import {environment} from "../../../environments/environment";
import {plainToClass} from "class-transformer";
import {map} from "rxjs/operators";

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
        return this.http.post(environment.apiUrl + '/agent', agent);
    }

}
