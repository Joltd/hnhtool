import {Injectable} from "@angular/core";
import {Point} from "../model/point";
import {Box} from "../model/box";
import {Entity} from "../model/entity";
import {BehaviorSubject, Observable} from "rxjs";
import {Command} from "../model/command";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {tap} from "rxjs/operators";

@Injectable()
export class ViewerService {

    public static readonly MULTIPLIER: number = 50;
    public static readonly ZOOM_LEVELS: number[] = [0.25, 0.5, 1, 2];
    public static readonly GRID_STEP: number = 1024;

    private _viewport: Viewport = new Viewport();
    private _mouse: Mouse = new Mouse();
    private _entityIdentity: number = 1;
    private _entities: Entity[] = [];
    private _space: number;

    private _hasSelection: boolean = false;
    private _isMovement: boolean = false;

    private _mode: Mode = 'COMMON';

    private _commands: Command[] = [];

    private _onClick: () => void = () => {};
    private _onHover: (entity: Entity[]) => void = () => {};
    private _onCancel: () => void = () => {};
    private _onModeChanged: BehaviorSubject<Mode> = new BehaviorSubject<Mode>('COMMON');

    private _tooltip: String[] = ['Test','Test2'];

    constructor(private http: HttpClient) {
        this.scheduleSavePreferences(this.space, this._viewport.offset, this._viewport.zoom);
    }

    private scheduleSavePreferences(space: number, offset: Point, zoom: number) {
        setTimeout(
            () => {
                if (this.space != space || !this._viewport.offset.equals(offset) || this._viewport.zoom != zoom) {
                    let toSave = {
                        space: this.space,
                        offset: this._viewport.offset,
                        zoom: this._viewport.zoom
                    }
                    this.http.post(environment.apiUrl + '/preferences', toSave).subscribe();
                }
                this.scheduleSavePreferences(this.space, this._viewport.offset, this._viewport.zoom);
            },
            1000
        )
    }

    load(): Observable<void> {
        return this.http.get<any>(environment.apiUrl + '/preferences')
            .pipe(tap(result => {
                this._space = result.space;
                this._viewport.offset = new Point(result.offset.x, result.offset.y);
                this._viewport.zoom = result.zoom;
            }));
    }

    get space(): number {
        return this._space;
    }

    get mode(): Mode {
        return this._mode;
    }
    set mode(value: Mode) {
        this._mode = value;
        if (value == 'COMMON') {
            this.commands = [];
        }
        this.onModeChanged.next(value);
    }

    get commands(): Command[] {
        return this._commands;
    }

    set commands(value: Command[]) {
        this._commands = value;
    }

    get mouse(): Mouse {
        return this._mouse;
    }
    set mouse(value: Mouse) {
        this._mouse = value;
    }

    get selectionArea(): Box {
        if (!this.mouse.worldOrigin) {
            return this.mouse.area;
        }
        return new Box(
            new Point(
                Math.min(this.mouse.worldOrigin.x, this.mouse.worldCurrent.x),
                Math.min(this.mouse.worldOrigin.y, this.mouse.worldCurrent.y)
            ),
            new Point(
                Math.max(this.mouse.worldOrigin.x, this.mouse.worldCurrent.x),
                Math.max(this.mouse.worldOrigin.y, this.mouse.worldCurrent.y)
            )
        )
    }

    get hasSelection(): boolean {
        return this._hasSelection;
    }
    set hasSelection(value: boolean) {
        this._hasSelection = value;
    }

    get isMovement(): boolean {
        return this._isMovement;
    }
    set isMovement(value: boolean) {
        this._isMovement = value;
    }

    get tooltip(): String[] {
        return this._tooltip;
    }

    set tooltip(value: String[]) {
        this._tooltip = value;
    }

    // ##################################################
    // #                                                #
    // #  Viewport                                      #
    // #                                                #
    // ##################################################

    get viewport(): Viewport {
        return this._viewport;
    }
    set viewport(value: Viewport) {
        this._viewport = value;
    }

    resizeViewport(screenWidth: number, screenHeight: number) {
        this.viewport.size = this.sizeScreenToWorld(screenWidth, screenHeight);
    }

    moveViewport(screenDelta: Point) {
        this.viewport.offset = this.sizeScreenToWorld(screenDelta)
            .negate()
            .add(this.viewport.offset);
    }

    moveViewportTo(worldPoint: Point) {
        this.viewport.offset = worldPoint.sub(this.viewport.size.div(2));
    }

    // ##################################################
    // #                                                #
    // #  Entities                                      #
    // #                                                #
    // ##################################################

    createEntity(): Entity {
        let entity = new Entity(this._entityIdentity++);
        this._entities.push(entity);
        return entity;
    }

    addEntity(entity: Entity) {
        let found = this._entities.find(_entity => _entity.id == entity.id);
        if (!found) {
            this._entities.push(entity);
        }
    }

    removeEntity(entity: Entity) {
        let index = this._entities.findIndex(_entity => _entity.id == entity.id);
        if (index >= 0) {
            this._entities.splice(index, 1);
        }
    }

    get entities(): Entity[] {
        return this._entities;
    }

    // ##################################################
    // #                                                #
    // #  Events                                        #
    // #                                                #
    // ##################################################

    set onClick(value: () => void) {
        this._onClick = value ? value : () => {};
    }
    get onClick(): () => void {
        return this._onClick;
    }

    set onHover(value: (entity: Entity[]) => void) {
        this._onHover = value ? value : () => {};
    }
    get onHover(): (entity: Entity[]) => void {
        return this._onHover;
    }

    set onCancel(value: () => void) {
        this._onCancel = value ? value : () => {};
    }
    get onCancel(): () => void {
        return this._onCancel;
    }

    get onModeChanged(): BehaviorSubject<Mode> {
        return this._onModeChanged;
    }

    // ##################################################
    // #                                                #
    // #  Conversation                                  #
    // #                                                #
    // ##################################################

    sizeScreenToWorld(screenWidth: number | Point, screenHeight?: number): Point {
        return ViewerService.toPoint(screenWidth, screenHeight)
            .mul(ViewerService.MULTIPLIER)
            .mul(ViewerService.ZOOM_LEVELS[this.viewport.zoom]);
    }

    sizeWorldToScreen(worldWidth: number | Point, worldHeight?: number): Point {
        return ViewerService.toPoint(worldWidth, worldHeight)
            .div(ViewerService.MULTIPLIER)
            .div(ViewerService.ZOOM_LEVELS[this.viewport.zoom]);
    }

    positionScreenToWorld(screenX: number | Point, screenY?: number): Point {
        return this.sizeScreenToWorld(screenX, screenY).add(this.viewport.offset);
    }

    positionWorldToScreen(worldX: number | Point, worldY?: number): Point {
        return this.sizeWorldToScreen(ViewerService.toPoint(worldX, worldY).sub(this.viewport.offset));
    }

    private static toPoint(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return value;
        } else if (y === undefined) {
            return new Point(value, value);
        } else {
            return new Point(value, y);
        }
    }
    
}

class Viewport {
    private _offset: Point = new Point(0,0);
    private _size: Point = new Point(1000, 1000);
    private _zoom: number = 1;

    get offset(): Point {
        return this._offset;
    }
    set offset(value: Point) {
        this._offset = value;
    }

    get size(): Point {
        return this._size;
    }
    set size(value: Point) {
        this._size = value;
    }

    get zoom(): number {
        return this._zoom;
    }
    set zoom(value: number) {
        this._zoom = value;
    }
}

class Mouse {
    private static readonly CURSOR_BOX = new Point(100, 100);

    private _lmb: boolean = false;
    private _mmb: boolean = false;
    private _rmb: boolean = false;
    private _screenCurrent: Point;
    private _worldOrigin: Point;
    private _worldCurrent: Point;
    private _worldCurrentRounded: Point;

    get lmb(): boolean {
        return this._lmb;
    }
    set lmb(value: boolean) {
        this._lmb = value;
    }

    get mmb(): boolean {
        return this._mmb;
    }
    set mmb(value: boolean) {
        this._mmb = value;
    }

    get rmb(): boolean {
        return this._rmb;
    }
    set rmb(value: boolean) {
        this._rmb = value;
    }

    get screenCurrent(): Point {
        return this._screenCurrent;
    }
    set screenCurrent(value: Point) {
        this._screenCurrent = value;
    }

    get worldOrigin(): Point {
        return this._worldOrigin;
    }
    set worldOrigin(value: Point) {
        this._worldOrigin = value;
    }

    get worldCurrent(): Point {
        return this._worldCurrent;
    }
    set worldCurrent(value: Point) {
        this._worldCurrent = value;
        if (value) {
            this._worldCurrentRounded = value.round(ViewerService.GRID_STEP).add(
                    (value.x >= 0 ? 1 : -1) * ViewerService.GRID_STEP / 2,
                    (value.y >= 0 ? 1 : -1) * ViewerService.GRID_STEP / 2
            );
        } else {
            this._worldCurrentRounded = null;
        }
    }

    get worldCurrentRounded(): Point {
        return this._worldCurrentRounded;
    }

    get area(): Box {
        if (!this.worldCurrent) {
            return null;
        }
        return new Box(
            this.worldCurrent.sub(Mouse.CURSOR_BOX),
            this.worldCurrent.add(Mouse.CURSOR_BOX)
        );
    }
}

export type Mode = 'COMMON' | 'WAREHOUSE' | 'CELL' | 'PATH' | 'AREA' | 'AREA_EDIT';