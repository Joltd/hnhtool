import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Task} from "../model/task";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {map} from "rxjs/operators";
import {plainToClass} from "class-transformer";
import {Page} from "../../core/page";
import {TaskLog} from "../model/task-log";

@Injectable()
export class TaskService {

    constructor(private http: HttpClient) {}

    list(page: number, size: number): Observable<Page<Task>> {
        let params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size));
        return this.http.get<any>(environment.apiUrl + '/task', {params})
            .pipe(
                map(result => {
                    return {
                        data: result.data.map(entry => plainToClass(Task, entry)),
                        total: result.total
                    }
                })
            );
    }

    loadLog(id: number): Observable<TaskLog[]> {
        if (!id) {
            return new Observable<TaskLog[]>(subscriber => subscriber.next([]));
        }
        return this.http.get<string>(environment.apiUrl + '/task/' + id + '/log')
            .pipe(map(result => {
                let data = JSON.parse(result);
                let taskLog = plainToClass(TaskLog, data);
                return taskLog.children;
            }));
    }

    loadScripts(): Observable<string[]> {
        return this.http.get<string[]>(environment.apiUrl + '/task/script');
    }

    schedule(script: string): Observable<void> {
        return this.http.post<void>(environment.apiUrl + '/task/script', script);
    }

}