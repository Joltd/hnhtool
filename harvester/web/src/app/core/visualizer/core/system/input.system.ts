import {Viewport} from "../common/viewport";
import {Point} from "../common/point";
import {Entity} from "../entity";
import {HoverableComponent} from "../component/hoverable.component";
import {SelectableComponent} from "../component/selectable.component";

export class InputSystem {

    private viewport: Viewport;

    private lmb: boolean = false;
    private mmb: boolean = false;
    private rmb: boolean = false;

    private mousePosition: Point;

    private readonly entities: Entity[];

    constructor(viewport: Viewport, entities: Entity[]) {
        this.viewport = viewport;
        this.entities = entities;
    }

    onMouseMove(event: MouseEvent) {
        this.viewport.cursor = this.viewport.positionScreenToWorld(event.offsetX, event.offsetY);
        let currentPosition = new Point(event.offsetX, event.offsetY);
        if (this.lmb) {
            let delta = currentPosition.sub(this.mousePosition);
            this.viewport.move(delta);
            this.mousePosition = currentPosition;
        }
    }

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
            this.handleSelection();
        } else if (event.button == 1) {
            this.mmb = false;
        } else if (event.button == 2) {
            this.rmb = false;
        }
    }

    onMouseWheel(event: WheelEvent) {
        let worldPosition = this.viewport.positionScreenToWorld(event.offsetX, event.offsetY);
        if (event.deltaY < 0) {
            this.viewport.zoomIn(worldPosition);
        } else {
            this.viewport.zoomOut(worldPosition);
        }
    }

    //

    private handleSelection() {
        let selectionChanged = false;
        for (let entity of this.entities) {
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
    }

}