import {ViewportService} from "./viewport.service";
import {Point} from "../model/point";
import {EntityService} from "./entity.service";
import {ModeService} from "./mode/mode.service";
import {Injectable} from "@angular/core";
import {SelectableComponent} from "../model/component/selectable.component";
import {HoverableComponent} from "../model/component/hoverable.component";
import {Renderable} from "../model/component/render/renderable";

@Injectable()
export class InputService {

    private lmb: boolean = false;
    private mmb: boolean = false;
    private rmb: boolean = false;

    private dragging: boolean = false;

    private mousePosition: Point;

    constructor(
        private viewportService: ViewportService,
        private entityService: EntityService,
        private modeService: ModeService
    ) {}

    onMouseDown(event: MouseEvent) {
        if (event.button == 0) {
            this.lmb = true;
            this.mousePosition = new Point(event.offsetX, event.offsetY);
        } else if (event.button == 1) {
            this.mmb = true;
        } else if (event.button == 2) {
            this.rmb = true;
        }
    }

    onMouseUp(event: MouseEvent) {
        if (event.button == 0) {
            this.lmb = false;
            this.mousePosition = null;
            let wasSelected = this.handleSelection();
            if (wasSelected) {
                this.fireEvent('SELECTION');
            }
            if (!this.dragging && !wasSelected) {
                this.fireEvent('CLICK');
            }
            this.dragging = false;
        } else if (event.button == 1) {
            this.mmb = false;
            this.mousePosition = null;
            this.dragging = false;
        } else if (event.button == 2) {
            this.rmb = false;
            this.fireEvent('CANCEL');
        }
    }

    onMouseMove(event: MouseEvent) {
        this.viewportService.cursor = this.viewportService.positionScreenToWorld(event.offsetX, event.offsetY);
        let currentPosition = new Point(event.offsetX, event.offsetY);
        if (this.lmb) {
            this.dragging = true;
            let delta = currentPosition.sub(this.mousePosition);
            delta = this.viewportService.sizeScreenToWorld(delta.x, delta.y);
            let moved = this.handleMoving(delta);
            if (moved) {
                this.fireEvent("MOVEMENT");
            }
            this.mousePosition = currentPosition;
        }
        if (this.mmb) {
            this.dragging = true;
            let delta = currentPosition.sub(this.mousePosition);
            this.viewportService.move(delta);
            this.mousePosition = currentPosition;
        }
        if (!this.lmb && !this.mmb && !this.rmb) {
            this.handleHovering();
        }
    }

    onMouseWheel(event: WheelEvent) {
        let worldPosition = this.viewportService.positionScreenToWorld(event.offsetX, event.offsetY);
        if (event.deltaY < 0) {
            this.viewportService.zoomIn(worldPosition);
        } else {
            this.viewportService.zoomOut(worldPosition);
        }
    }

    private fireEvent(event: Event) {
        let currentMode = this.modeService.getCurrentMode();
        if (!currentMode) {
            return;
        }

        let listener = currentMode.listener(event);
        if (!listener) {
            return;
        }

        listener();
    }

    private handleHovering() {
        for (let entity of this.entityService.entities()) {
            let hoverable = entity.getComponent(HoverableComponent)
            if (!hoverable) {
                continue;
            }

            let renderable = entity.getComponent(Renderable);
            if (!renderable) {
                hoverable.hovered = false;
                continue;
            }

            hoverable.hovered = renderable.isIntersect(this.viewportService.cursor);
        }
    }

    private handleSelection(): boolean {
        let selectionChanged = false;
        for (let entity of this.entityService.entities()) {
            let hoverableComponent = entity.getComponent(HoverableComponent);
            let selectableComponent = entity.getComponent(SelectableComponent);
            if (!selectableComponent) {
                continue;
            }

            if (!hoverableComponent || !hoverableComponent.hovered) {
                selectableComponent.selected = false;
                continue
            }

            if (selectableComponent.selected) {
                selectableComponent.selected = false;
            } else if(!selectionChanged) {
                selectableComponent.selected = true;
                selectionChanged = true;
            }
        }
        return selectionChanged;
    }

    private handleMoving(delta: Point): boolean {
        let moved = false;
        for (let entity of this.entityService.entities()) {
            let selectableComponent = entity.getComponent(SelectableComponent);
            let renderable = entity.getComponent(Renderable);
            if (!selectableComponent || !renderable) {
                continue;
            }

            renderable.move(delta);
            moved = true;
        }
        return moved;
    }



}

export type Event = 'SELECTION' | 'MOVEMENT' | 'CLICK' | 'CANCEL';