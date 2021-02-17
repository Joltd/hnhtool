import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ViewerService} from "./viewer.service";
import {Entity} from "../model/entity";
import {Command} from "../model/command";
import {environment} from "../../../environments/environment";
import {map} from "rxjs/operators";
import {Area} from "../model/area";
import {Point} from "../model/point";
import {Disabled, FollowCursor, Hoverable, Movement, Position, Selectable} from "../model/components";

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
                this.viewerService.commands.push(new Command('wallpaper', () => this.enter()));
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
            .set('space', this.viewerService.space.id.toString());
        this.http.get<any[]>(environment.apiUrl + '/area', {params})
            .pipe(map(result => result.map(entry => this.toAreaEntity(entry))))
            .subscribe(result => this._areas = result);
    }

    private toAreaEntity(entry: any): Entity {
        let entity = this.viewerService.createEntity();
        this._areas.push(entity);
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

    done() {
        for (let area of this._areas) {
            area.remove(Hoverable);
            area.remove(Selectable);
        }
        this.viewerService.mode = 'COMMON';
    }

    startAdd() {
        this._from = this.viewerService.createFollowPrimitiveEntity();

        this.viewerService.onClick = () => this.addFrom();
        this.viewerService.onCancel = () => this.stopAdd();

        this.viewerService.mode = 'AREA_EDIT';
    }

    private addFrom() {
        this._from.remove(FollowCursor);

        this._to = this.viewerService.createFollowPrimitiveEntity();

        this._area = this.viewerService.createEntity();
        let area = this._area.add(new Area());
        area.from = this._from.get(Position);
        area.to = this._to.get(Position);
        area.to.value = area.from.value;

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
            if (entity.id != this._area.id) {
                entity.add(new Disabled());
            }
        }

        let area = this._area.get(Area);

        this._from = this.viewerService.createMovementPrimitiveEntity();
        this._from.add(area.from);

        this._to = this.viewerService.createMovementPrimitiveEntity();
        this._to.add(area.to);

        this.viewerService.mode = 'AREA_EDIT';
    }

    remove() {
        let newAreas: Entity[] = [];
        for (let area of this._areas) {
            let selected = area.get(Selectable)?.value;
            if (selected) {
                this.http.delete(environment.apiUrl + '/area/' + area.get(Area).id).subscribe(() => {
                    this.viewerService.removeEntity(area);
                })
            } else {
                newAreas.push(area);
            }
        }
        this._areas = newAreas;
    }

    apply() {

        let area = this._area.get(Area);
        let toSave = {
            id: area.id,
            spaceId: this.viewerService.space.id,
            from: {
                x: area.from.value.x,
                y: area.from.value.y
            },
            to: {
                x: area.to.value.x,
                y: area.to.value.y
            }
        };

        this.http.post<any>(environment.apiUrl + '/area', toSave)
            .subscribe(result => {
                area.id = result.id;

                this._area.add(new Hoverable());
                this._area.add(new Selectable());

                this._areas.push(this._area);

                this.cancel();
            });

    }

    cancel() {
        if (!this._area.get(Area).id) {
            this.viewerService.removeEntity(this._area);
        }
        this.viewerService.removeEntity(this._from);
        this.viewerService.removeEntity(this._to);
        this._from = null;
        this._to = null;
        this._area = null;
        this.viewerService.mode = 'AREA';
    }

}