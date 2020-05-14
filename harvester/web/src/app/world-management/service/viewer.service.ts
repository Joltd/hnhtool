import {Injectable, Type} from "@angular/core";
import {Viewport} from "../model/viewport";
import {Point} from "../model/point";
import {Mouse} from "../model/mouse";
import {Entity} from "../model/entity";
import {Renderable} from "../model/component/render/renderable";
import {SelectableComponent} from "../model/component/selectable.component";
import {HoverableComponent} from "../model/component/hoverable.component";
import {Box} from "../model/box";

@Injectable()
export class ViewerService {

    private viewport: Viewport = new Viewport();
    private mouse: Mouse = new Mouse();

    private nextEntityId: number = 1;
    private entities: Entity[] = [];

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
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.screenY);
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
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.screenY);
        this.mouse.origin = null;
    }

    onMouseMove(event: MouseEvent) {
        this.mouse.dragging = true;
        let cursor = this.positionScreenToWorld(event.offsetX, event.screenY);
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
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.screenY);
    }

    onMouseWheel(event: WheelEvent) {
        this.mouse.cursor = this.positionScreenToWorld(event.offsetX, event.offsetY);
        if (event.deltaY < 0) {
            this.zoomIn();
        } else {
            this.zoomOut();
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
        this.entities.push(entity);
        return entity;
    }

    queryEntities<T>(componentType: Type<T>): Entity[] {
        return this.entities.filter(entity => entity.getComponent(componentType))
    }

    // ##################################################
    // #                                                #
    // #  Selection                                     #
    // #                                                #
    // ##################################################

    private handleSelection() {

        let selectionArea = this.mouse.getSelectionArea();
        if (!selectionArea) {
            selectionArea = new Box(this.mouse.cursor.sub(100), this.mouse.cursor.add(100));
        }

        let wasSelected = false;
        for (let entity of this.entities) {
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
            this.mouse.cursor.sub(100),
            this.mouse.cursor.add(100)
        );

        for (let entity of this.entities) {
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