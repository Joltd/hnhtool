import {Injectable} from "@angular/core";
import {ViewerService} from "./viewer.service";
import {Disabled, FollowCursor, Hoverable, Movement, Position, Primitive, Selectable} from "../model/components";
import {Entity} from "../model/entity";
import {Point} from "../model/point";
import {HttpClient} from "@angular/common/http";
import {Cell, Warehouse} from "../model/warehouse";
import {Command} from "../model/command";

@Injectable()
export class WarehouseService {

    private _warehouses: Entity[] = [];

    private _warehouse: Entity;
    private _cells: Entity[] = [];
    private _dummy: Entity;

    constructor(
        private http: HttpClient,
        private viewerService: ViewerService
    ) {
        this.viewerService.onModeChanged.subscribe((mode) => {
            if (mode == 'COMMON') {
                this.viewerService.commands.push(new Command('storefront', () => this.enter()));
            }
        });
    }

    enter() {
        this.viewerService.commands = [
            new Command('add', () => this.add()),
            new Command('edit', () => this.edit()),
            new Command('remove', () => this.remove()),
            new Command('done', () => this.done())
        ];
        for (let warehouse of this._warehouses) {
            warehouse.add(new Hoverable());
            warehouse.add(new Selectable());
        }
    }

    load() {
        // this.http.get<any[]>(environment + '/warehouse')
        //     .pipe(map(result => result.map(entry => this.toWarehouseEntity(entry))))
        //     .subscribe(result => this._warehouses = result);
    }

    private toWarehouseEntity(entry: any) {
        let entity = this.viewerService.createEntity();
        let warehouse = entity.add(new Warehouse());
        warehouse.id = entry.id;
        warehouse.cells = entry.cells.map(cellEntry => {
            let cell = new Cell();
            cell.id = cellEntry.id;
            cell.position = new Point(cellEntry.x, cellEntry.y);
            return cell;
        });
        return entity;
    }

    // ##################################################
    // #                                                #
    // #  Warehouse                                     #
    // #                                                #
    // ##################################################

    add() {
        this._warehouse = this.viewerService.createEntity();
        this._warehouse.add(new Warehouse());
        this._warehouse.add(new Hoverable());
        this._warehouse.add(new Selectable());
        this._warehouses.push(this._warehouse);

        for (let entity of this._warehouses) {
            entity.add(new Disabled());
        }

        this.viewerService.mode = 'CELL';
    }

    edit() {
        this._warehouse = this._warehouses.find(entity => {
            let selectable = entity.get(Selectable);
            return entity.get(Warehouse) && selectable && selectable.value;
        });

        if (!this._warehouse) {
            return;
        }

        for (let entity of this.viewerService.entities) {
            entity.add(new Disabled());
        }

        for (let cell of this._warehouse.get(Warehouse).cells) {
            let cellEntity = this.viewerService.createEntity();
            this._cells.push(cellEntity);

            cellEntity.add(new Hoverable());
            cellEntity.add(new Selectable());
            cellEntity.add(new Movement());
            cellEntity.add(new Primitive());
            cellEntity.add(new Position()).value = cell.position;
        }

        this.viewerService.mode = 'CELL';

        this.viewerService.commands = [
            new Command('add', () => this.startAddCell()),
            new Command('remove', () => this.removeCell()),
            new Command('done', () => this.apply()),
            new Command('close', () => this.cancel())
        ];
    }

    remove() {

    }

    done() {
        for (let warehouse of this._warehouses) {
            warehouse.remove(Hoverable);
            warehouse.remove(Selectable);
        }
        this.viewerService.mode = 'COMMON';
    }

    // ##################################################
    // #                                                #
    // #  Cell                                          #
    // #                                                #
    // ##################################################

    startAddCell() {
        this.createDummy();
        for (let cell of this._cells) {
            cell.add(Disabled);
        }
        this.viewerService.onClick = () => this.addCell();
    }

    addCell() {
        for (let cell of this._cells) {
            cell.remove(Disabled);
        }
        this.dummyToCell();
        this.viewerService.onClick = null;
    }

    removeCell() {
        let newCells = [];
        for (let cell of this._cells) {
            let selectable = cell.get(Selectable);
            if (selectable && selectable.value) {
                this.viewerService.removeEntity(cell);
            } else {
                newCells.push(cell);
            }
        }
        this._cells = newCells;
    }

    apply() {
        let warehouse = this._warehouse.get(Warehouse);
        warehouse.cells = [];
        for (let cellEntity of this._cells) {
            let cell = new Cell();
            cell.position = cellEntity.get(Position).value;
            warehouse.cells.push(cell);
        }
        this.cancel();
    }

    cancel() {
        for (let cell of this._cells) {
            this.viewerService.removeEntity(cell);
        }
        this._cells = [];
        this._warehouse = null;
        for (let entity of this.viewerService.entities) {
            entity.remove(Disabled);
        }

        this.viewerService.mode = 'WAREHOUSE';
    }

    //

    private createCell(): Entity {
        let cell = this.viewerService.createEntity();
        cell.add(new Hoverable());
        cell.add(new Selectable());
        cell.add(new Movement());
        cell.add(new Primitive());
        cell.add(new Position());
        this._cells.push(cell);
        return cell;
    }

    private createDummy(): Entity {
        let dummy = this.viewerService.createEntity();
        dummy.add(new FollowCursor());
        dummy.add(new Primitive());
        dummy.add(new Position()).value = new Point(0,0);
        this._dummy = dummy;
        return dummy;
    }

    private dummyToCell() {
        if (!this._dummy) {
            return;
        }

        this._dummy.remove(FollowCursor);
        this._dummy.add(new Hoverable());
        this._dummy.add(new Selectable());
        this._dummy.add(new Movement());
        this._cells.push(this._dummy);
        this._dummy = null;
    }

}

