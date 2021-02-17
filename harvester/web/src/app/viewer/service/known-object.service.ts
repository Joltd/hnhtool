import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ViewerService} from "./viewer.service";
import {environment} from "../../../environments/environment";
import {Hoverable, Position, Primitive, Selectable, Tooltip} from "../model/components";
import {Point} from "../model/point";
import {Entity} from "../model/entity";
import {KnownObject} from "../model/known-object";

@Injectable()
export class KnownObjectService {

    private _knownObjects: Entity[] = [];

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
        this.viewerService.onModeChanged.subscribe((mode) => {
            for (let knownObjectEntity of this._knownObjects) {
                let knownObject = knownObjectEntity.get(KnownObject);
                if (mode == 'COMMON') {
                    knownObjectEntity.add(new Hoverable());
                    knownObjectEntity.remove(Selectable);
                } else if (mode == 'PATH' && knownObject.doorway) {
                    knownObjectEntity.add(new Selectable());
                } else {
                    knownObjectEntity.remove(Hoverable);
                }
            }
        })
    }

    load() {
        let params = new HttpParams().set('space', this.viewerService.space.id.toString());
        this.http.get<any[]>(environment.apiUrl + '/known-object', {params})
            .subscribe(result => {
                for (let entry of result) {
                    let knownObjectEntity = this.viewerService.createEntity();
                    knownObjectEntity.add(new Position()).value = new Point(entry.x, entry.y);
                    knownObjectEntity.add(new Tooltip()).value = entity => entity.get(Position).value.toString() + ' ' + entry.resource;
                    knownObjectEntity.add(new Hoverable());

                    let knownObject = knownObjectEntity.add(new KnownObject());
                    knownObject.id = entry.id;

                    let primitive = knownObjectEntity.add(new Primitive());
                    primitive.type = 'CIRCLE';
                    primitive.size = 200;
                    primitive.color = '#000000';

                    this._knownObjects.push(knownObjectEntity);
                }
            })
    }

}