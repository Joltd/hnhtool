import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {LearningStat} from "../model/learning-stat";
import {Page} from "../../core/page";
import {environment} from "../../../environments/environment";
import {map} from "rxjs/operators";
import {classToPlain, plainToClass} from "class-transformer";
import {Learning} from "../model/learning";

@Injectable()
export class LearningService {

    constructor(private http: HttpClient) {}

    byId(id: number): Observable<Learning> {
        return this.http.get<any>(environment.apiUrl + '/learning/' + id)
            .pipe(map(result => plainToClass(Learning, result)));
    }

    listStat(id: number, page: number, size: number): Observable<Page<LearningStat>> {
        let params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size));
        return this.http.get<any>(environment.apiUrl + '/learning/' + id + '/stat', {params})
            .pipe(
                map(result => {
                    return {
                        data: result.data.map(entry => plainToClass(LearningStat, entry)),
                        total: result.total
                    }
                })
            );
    }

    update(learning: Learning): Observable<any> {
        return this.http.post(environment.apiUrl + '/learning', classToPlain(learning));
    }

}