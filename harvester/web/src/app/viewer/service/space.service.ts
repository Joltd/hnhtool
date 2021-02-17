import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Space} from "../model/space";
import {environment} from "../../../environments/environment";
import {plainToClass} from "class-transformer";
import {map} from "rxjs/operators";

@Injectable()
export class SpaceService {

    private _spaces: Space[] = [];

    constructor(private http: HttpClient) {
    }

    load() {
        return this.http.get<any[]>(environment.apiUrl + '/space')
            .pipe(map(result => result.map(entry => plainToClass(Space, entry))))
            .subscribe(result => this._spaces = result)
    }

    get spaces(): Space[] {
        return this._spaces;
    }
}