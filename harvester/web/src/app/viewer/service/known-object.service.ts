import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ViewerService} from "./viewer.service";
import {environment} from "../../../environments/environment";
import {Hoverable, Position, Primitive, Tooltip} from "../model/components";
import {Point} from "../model/point";

@Injectable()
export class KnownObjectService {

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
    }

    load() {
        let params = new HttpParams().set('space', this.viewerService.space.toString());
        this.http.get<any[]>(environment.apiUrl + '/known-object', {params})
            .subscribe(result => {
                for (let entry of result) {
                    let knownObject = this.viewerService.createEntity();
                    knownObject.add(new Position()).value = new Point(entry.x, entry.y);
                    knownObject.add(new Tooltip()).value = entity => entity.get(Position).value.toString() + ' ' + entry.resource;
                    knownObject.add(new Hoverable());

                    let primitive = knownObject.add(new Primitive());
                    primitive.type = 'CIRCLE';
                    primitive.size = 200;
                    primitive.color = '#000000'
                }
            })
    }

}