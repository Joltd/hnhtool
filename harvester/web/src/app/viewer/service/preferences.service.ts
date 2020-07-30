import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Point} from "../model/point";

@Injectable()
export class PreferencesService {

    private _spaceId: number;
    private _offset: Point;
    private _zoom: number;

    constructor(
        private http: HttpClient
    ) {}

    load() {

    }

    get spaceId(): number {
        return this._spaceId;
    }

    get offset(): Point {
        return this._offset;
    }

    get zoom(): number {
        return this._zoom;
    }
}