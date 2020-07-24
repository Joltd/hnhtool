import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ViewerService} from "./viewer.service";

@Injectable()
export class KnownObjectService {

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
    }

    load() {

    }

}