import {Entity} from "../model/entity";
import {PointComponent} from "../model/component/render/point.component";
import {LineComponent} from "../model/component/render/line.component";
import {SelectableComponent} from "../model/component/selectable.component";
import {HoverableComponent} from "../model/component/hoverable.component";
import {Point} from "../model/point";
import {Injectable} from "@angular/core";
import {ViewerService} from "./viewer.service";
import {ColorUtil} from "./color.util";

@Injectable()
export class RenderService {

    private static readonly SELECTION: string = '#00FF00';

    constructor(private viewerService: ViewerService) {}

    render(entity: Entity) {
        let point = entity.getComponent(PointComponent);
        if (point) {
            this.renderPoint(entity);
            return;
        }

        let line = entity.getComponent(LineComponent);
        if (line) {
            this.renderLine(entity);
            return;
        }
    }

    private renderPoint(entity: Entity) {
        let point = entity.getComponent(PointComponent);
        let selectable = entity.getComponent(SelectableComponent);
        let hoverable = entity.getComponent(HoverableComponent);

        if (!point) {
            return;
        }

        let canvas = this.viewerService.canvas;

        canvas.fillStyle = hoverable && hoverable.hovered
            ? ColorUtil.lighten(point.color)
            : point.color;

        let position = this.viewerService.positionWorldToScreen(point.position);
        let size = this.viewerService.sizeWorldToScreen(new Point(point.size, 0)).x;
        canvas.beginPath();
        canvas.arc(position.x, position.y, size, 0, 2 * Math.PI);
        canvas.fill();

        if (selectable && selectable.selected) {
            canvas.strokeStyle = RenderService.SELECTION;
            canvas.beginPath();
            canvas.arc(position.x, position.y, size, 0, 2 * Math.PI);
            canvas.stroke();
        }
    }

    private renderLine(entity: Entity) {
        let line = entity.getComponent(LineComponent);
        let selectable = entity.getComponent(SelectableComponent);
        let hoverable = entity.getComponent(HoverableComponent);

        if (!line) {
            return;
        }

        let canvas = this.viewerService.canvas;

        canvas.strokeStyle = hoverable && hoverable.hovered
            ? ColorUtil.lighten(line.color)
            : line.color;

        let screenFrom = this.viewerService.positionWorldToScreen(line.from);
        let screenTo = this.viewerService.positionWorldToScreen(line.to);

        canvas.beginPath();
        canvas.moveTo(screenFrom.x, screenFrom.y);
        canvas.lineTo(screenTo.x, screenTo.y);
        canvas.stroke();

        if (selectable && selectable.selected) {
            canvas.strokeStyle = RenderService.SELECTION;
            canvas.beginPath();
            canvas.moveTo(screenFrom.x, screenFrom.y);
            canvas.lineTo(screenTo.x, screenTo.y);
            canvas.stroke();
        }
    }

}