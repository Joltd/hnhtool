import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ViewerService} from "./viewer.service";
import {Entity} from "../model/entity";
import {Command} from "../model/command";
import {environment} from "../../../environments/environment";
import {map} from "rxjs/operators";
import {Area} from "../model/area";
import {Point} from "../model/point";
import {Disabled, FollowCursor, Hoverable, Movement, Position, Primitive, Selectable} from "../model/components";

@Injectable()
export class AreaService {

    private _areas: Entity[] = [];

    private _area: Entity;
    private _from: Entity;
    private _to: Entity;

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
        this.viewerService.onModeChanged.subscribe(mode => {
            if (mode == 'COMMON') {
                this.viewerService.commands.push(new Command('storefront', () => this.enter()));
            } else if (mode == 'AREA') {
                this.viewerService.commands = [
                    new Command('add', () => this.startAdd()),
                    new Command('edit', () => this.edit()),
                    new Command('remove', () => this.remove()),
                    new Command('done', () => this.done())
                ]
            } else if (mode == 'AREA_EDIT') {
                this.viewerService.commands = [
                    new Command('done', () => this.apply()),
                    new Command('close', () => this.cancel())
                ];
            }
        })
    }

    load() {
        let params = new HttpParams()
            .set('space', this.viewerService.space.toString());
        this.http.get<any[]>(environment.apiUrl + '/area', {params})
            .pipe(map(result => result.map(entry => this.toAreaEntity(entry))))
            .subscribe(result => this._areas = result);
    }

    private toAreaEntity(entry: any): Entity {
        let entity = this.viewerService.createEntity();
        let area = entity.add(new Area());
        area.id = entry.id;
        area.from.value = new Point(entry.from.x, entry.from.y);
        area.to.value = new Point(entry.to.x, entry.to.y);
        return entity;
    }

    enter() {
        this.viewerService.mode = 'AREA';
        for (let area of this._areas) {
            area.add(new Hoverable());
            area.add(new Selectable());
        }
    }

    startAdd() {
        this.viewerService.mode = 'AREA_EDIT';

        this._from = this.viewerService.createEntity();
        this._from.add(new Primitive());
        this._from.add(new Position());
        this._from.add(new FollowCursor());

        this.viewerService.onClick = () => this.addFrom();
        this.viewerService.onCancel = () => this.stopAdd();
    }

    private addFrom() {
        this._from.remove(FollowCursor);

        this._to = this.viewerService.createEntity();
        this._to.add(new Primitive());
        this._to.add(new Position());
        this._to.add(new FollowCursor());

        this._area = this.viewerService.createEntity();
        let area = this._area.add(new Area());
        area.from = this._from.get(Position);
        area.to = this._to.get(Position);

        this.viewerService.onClick = () => this.addTo();
    }

    private addTo() {
        this._to.remove(FollowCursor);

        this._from.add(new Hoverable());
        this._from.add(new Selectable());
        this._from.add(new Movement());

        this._to.add(new Hoverable());
        this._to.add(new Selectable());
        this._to.add(new Movement());

        this.viewerService.onClick = null;
        this.viewerService.onCancel = null;
    }

    private stopAdd() {
        this.viewerService.removeEntity(this._from);
        if (this._to) {
            this.viewerService.removeEntity(this._to);
        }
        if (this._area) {
            this.viewerService.removeEntity(this._area);
        }
        this._from = null;
        this._to = null;
        this._area = null;
        this.viewerService.onClick = null;
        this.viewerService.onCancel = null;

        this.viewerService.mode = 'AREA';
    }

    edit() {
        this._area = this._areas.find(entity => {
            let selectable = entity.get(Selectable);
            return entity.get(Area) && selectable && selectable.value;
        });

        if (!this._area) {
            return;
        }

        for (let entity of this.viewerService.entities) {
            entity.add(new Disabled());
        }

        let area = this._area.get(Area);

        this._from = this.viewerService.createEntity();
        this._from.add(new Primitive());
        this._from.add(area.from);
        this._from.add(new Hoverable());
        this._from.add(new Selectable());
        this._from.add(new Movement());

        this._to = this.viewerService.createEntity();
        this._to.add(new Primitive());
        this._to.add(area.to);
        this._to.add(new Hoverable());
        this._to.add(new Selectable());
        this._to.add(new Movement());

        this.viewerService.mode = 'AREA_EDIT';
    }

    remove() {

    }

    done() {
        for (let area of this._areas) {
            area.remove(Hoverable);
            area.remove(Selectable);
        }
        this.viewerService.mode = 'COMMON';
    }

    apply() {

        this.cancel();
    }

    cancel() {
        this.viewerService.removeEntity(this._from);
        this.viewerService.removeEntity(this._to);
        this._from = null;
        this._to = null;
        this._area = null;
        this.viewerService.mode = 'AREA';
    }

}