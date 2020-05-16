import {Injectable, Predicate, Type} from "@angular/core";
import {Viewport} from "../model/viewport";
import {Point} from "../model/point";
import {Mouse} from "../model/mouse";
import {Entity} from "../model/entity";
import {Renderable} from "../model/component/render/renderable";
import {SelectableComponent} from "../model/component/selectable.component";
import {HoverableComponent} from "../model/component/hoverable.component";
import {Box} from "../model/box";
import {Mode} from "./mode/mode.service";

@Injectable()
export class ViewerService {

    private static readonly CURSOR_BOX = new Point(100, 100);

    private _canvas: CanvasRenderingContext2D;

    private viewport: Viewport = new Viewport();
    private mouse: Mouse = new Mouse();

    private nextEntityId: number = 1;
    private entities: Map<number, Entity> = new Map<number, Entity>();

    private _mode: Mode;

    // ##################################################
    // #                                                #
    // #  Properties                                    #
    // #                                                #
    // ##################################################

    get canvas(): CanvasRenderingContext2D {
        return this._canvas;
    }

    set canvas(value: CanvasRenderingContext2D) {
        this._canvas = value;
    }

    get mode(): Mode {
        return this._mode;
    }

    set mode(value: Mode) {
        this._mode = value;
    }

    // ##################################################
    // #                                                #
    // #  Handlers                                      #
    // #                                                #
    // ##################################################

    onMouseDown(event: MouseEvent) {
        if (event.button == 0) {
            this.mouse.lmb = true;
        } else if (event.button == 1) {
            this.mouse.mmb = true;
        } else if (event.button == 2) {
            this.mouse.rmb = true;
        }
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
    }

    onMouseUp(event: MouseEvent) {
        this.mouse.dragging = false;
        if (event.button == 0) {
            this.handleSelection();
            this.mouse.lmb = false;
        } else if (event.button == 1) {
            this.mouse.mmb = false;
        } else if (event.button == 2) {
            this.mouse.rmb = false;
        }
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
        this.mouse.origin = null;
    }

    onMouseMove(event: MouseEvent) {
        this.mouse.dragging = true;
        let cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
        if (this.mouse.lmb) {
            if (!this.mouse.origin) {
                this.mouse.origin = this.mouse.cursor;
            }
        } else if (this.mouse.mmb) {
            let delta = cursor.sub(this.mouse.cursor);
            this.moveViewport(delta);
        } else if (this.mouse.rmb) {

        } else {
            this.mouse.cursor = cursor;
            this.handleHovering();
            this.mouse.dragging = false;
        }
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
    }

    onMouseWheel(event: WheelEvent) {
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
        if (event.deltaY < 0) {
            this.zoomIn();
        } else {
            this.zoomOut();
        }
    }

    onKeyUp(event: KeyboardEvent) {
        let command = this.mode.commands().find(command => command.enabled && command.key == event.key);
        if (command) {
            command.perform();
        }
    }

    // ##################################################
    // #                                                #
    // #  Convert                                       #
    // #                                                #
    // ##################################################

    sizeScreenToWorld(screenWidth: number, screenHeight: number): Point {
        return new Point(screenWidth, screenHeight)
            .mul(Viewport.MULTIPLIER)
            .mul(Viewport.ZOOM_LEVELS[this.viewport.zoom]);
    }

    sizeWorldToScreen(worldSize: Point): Point {
        return worldSize
            .div(Viewport.MULTIPLIER)
            .div(Viewport.ZOOM_LEVELS[this.viewport.zoom]);
    }

    positionScreenToWorld(screenX: number, screenY: number): Point {
        return this.sizeScreenToWorld(screenX, screenY).add(this.viewport.offset);
    }

    positionWorldToScreen(worldPosition: Point): Point {
        return this.sizeWorldToScreen(worldPosition.sub(this.viewport.offset));
    }

    // ##################################################
    // #                                                #
    // #  Viewport                                      #
    // #                                                #
    // ##################################################

    viewportOffset(): Point {
        return this.viewport.offset;
    }

    viewportSize(): Point {
        return this.viewport.size;
    }

    moveViewport(worldDelta: Point) {
        this.viewport.offset = worldDelta.negate()
            .add(this.viewport.offset);
    }

    resizeViewport(screenWidth: number, screenHeight: number) {
        this.viewport.size = this.sizeScreenToWorld(screenWidth, screenHeight);
    }

    zoomIn() {
        if (this.viewport.zoom == 0) {
            return;
        }

        this.viewport.zoom--;
        this.viewport.size = this.viewport.size.div(2);
        this.viewport.offset = this.mouse.cursor.sub(this.viewport.size.div(2));
    }

    zoomOut() {
        if (this.viewport.zoom == Viewport.ZOOM_LEVELS.length - 1) {
            return;
        }

        this.viewport.zoom++;
        this.viewport.size = this.viewport.size.mul(2);
        this.viewport.offset = this.mouse.cursor.sub(this.viewport.size.div(2));
    }

    // ##################################################
    // #                                                #
    // #  Mouse                                         #
    // #                                                #
    // ##################################################

    selectionArea(): Box {
        return this.mouse.getSelectionArea();
    }

    // ##################################################
    // #                                                #
    // #  Entity                                        #
    // #                                                #
    // ##################################################

    createEntity(): Entity {
        let entity = new Entity(this.nextEntityId++);
        this.entities.set(entity.id, entity);
        return entity;
    }

    queryEntitiesByComponent<T>(component: Type<T>) {
        return this.queryEntities((entity: Entity) => entity.getComponent(component) != null);
    }

    queryEntities(predicate: Predicate<Entity>): Entity[] {
        let result: Entity[] = [];
        for (let entity of this.entities.values()) {
            if (predicate(entity)) {
                result.push(entity);
            }
        }
        return result;
    }

    removeEntity(id: number) {
        this.entities.delete(id);
    }

    // ##################################################
    // #                                                #
    // #  Selection                                     #
    // #                                                #
    // ##################################################

    private handleSelection() {

        let selectionArea = this.mouse.getSelectionArea();
        if (!selectionArea) {
            selectionArea = new Box(
                this.mouse.cursor.sub(ViewerService.CURSOR_BOX),
                this.mouse.cursor.add(ViewerService.CURSOR_BOX)
            );
        }

        let wasSelected = false;
        for (let entity of this.entities.values()) {
            let renderable = entity.getComponent(Renderable);
            let selectableComponent = entity.getComponent(SelectableComponent);
            if (!selectableComponent) {
                continue;
            }

            if (!renderable) {
                selectableComponent.selected = false;
                continue;
            }

            selectableComponent.selected = renderable.isIntersect(selectionArea);
            wasSelected = wasSelected || selectableComponent.selected;
        }

    }

    // ##################################################
    // #                                                #
    // #  Hovering                                      #
    // #                                                #
    // ##################################################

    private handleHovering() {
        let hoverArea = new Box(
            this.mouse.cursor.sub(ViewerService.CURSOR_BOX),
            this.mouse.cursor.add(ViewerService.CURSOR_BOX)
        );

        for (let entity of this.entities.values()) {
            let renderable = entity.getComponent(Renderable);
            let hoverableComponent = entity.getComponent(HoverableComponent);
            if (!hoverableComponent) {
                continue;
            }

            if (!renderable) {
                hoverableComponent.hovered = false;
                continue;
            }

            hoverableComponent.hovered = renderable.isIntersect(hoverArea);
        }
    }

}