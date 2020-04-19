import {Entity} from "../entity";
import {PointComponent} from "../component/point.component";
import {PositionComponent} from "../component/position.component";
import {HoverableComponent} from "../component/hoverable.component";
import {SelectableComponent} from "../component/selectable.component";
import {ColorUtils} from "./color.utils";
import {Viewport} from "../common/viewport";
import {Point} from "../common/point";

export class PointRenderSystem {

    private readonly viewport: Viewport;

    constructor(viewport: Viewport) {
        this.viewport = viewport;
    }

    process(entity: Entity, context: CanvasRenderingContext2D) {
        let pointComponent = entity.getComponent(PointComponent);
        let positionComponent = entity.getComponent(PositionComponent);
        if (pointComponent == null || positionComponent == null) {
            return
        }

        // if out of viewport then return

        let position = this.viewport.positionWorldToScreen(positionComponent.position);
        let size = this.viewport.sizeWorldToScreen(new Point(pointComponent.size, 0)).x;

        let selectableComponent = entity.getComponent(SelectableComponent);

        if (selectableComponent && selectableComponent.selected) {
            context.strokeStyle = '#FFFF00'
            context.lineWidth = 3;
            switch (pointComponent.style) {
                case 'circle':
                    context.beginPath();
                    context.arc(position.x, position.y, size / 2, 0, Math.PI * 2);
                    context.stroke();
                    break;
                case 'rect':
                    context.beginPath();
                    context.moveTo(position.x, position.y);
                    context.lineTo(position.x + size, position.y);
                    context.lineTo(position.x + size, position.y + size);
                    context.lineTo(position.x, position.y + size);
                    context.closePath();
                    context.stroke();
                    break;
                default:
                    return;
            }
        }

        context.fillStyle = pointComponent.color;

        let hoverableComponent = entity.getComponent(HoverableComponent);

        if (hoverableComponent) {
            hoverableComponent.hovered = PointRenderSystem.isIntersectWithCursor(positionComponent.position, pointComponent.size, this.viewport.cursor)
            if (hoverableComponent.hovered) {
                context.fillStyle = ColorUtils.lighten(pointComponent.color);
            }
        }

        switch (pointComponent.style) {
            case 'circle':
                context.beginPath();
                context.arc(position.x, position.y, size / 2, 0, Math.PI * 2);
                context.fill();
                break;
            case 'rect':
                context.beginPath();
                context.moveTo(position.x, position.y);
                context.lineTo(position.x + size, position.y);
                context.lineTo(position.x + size, position.y + size);
                context.lineTo(position.x, position.y + size);
                context.closePath();
                context.fill()
                break;
            default:
                return;
        }

    }

    private static isIntersectWithCursor(position: Point, size: number, cursor: Point): boolean {
        let left = position.x - size / 2;
        let right = left + size;
        let top = position.y - size / 2;
        let bottom = top + size;
        return cursor.x >= left && cursor.x <= right && cursor.y >= top && cursor.y <= bottom;
    }

}