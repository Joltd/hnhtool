import {Point} from "../../point";
import {Renderable} from "./renderable";
import {ViewerService} from "../../../service/viewer.service";
import {Box} from "../../box";

export class LineComponent extends Renderable {

    private _from: Point = new Point(0,0);
    private _to: Point = new Point(0,0);
    private _color: string = '#00FF00';
    private _width: number = 100;

    isIntersect(box: Box): boolean {
        return box.isContains(this._from) || box.isContains(this._to);
    }

    render(context: CanvasRenderingContext2D, viewerService: ViewerService) {
        context.strokeStyle = this._color;
        context.beginPath();
        let screenFrom = viewerService.positionWorldToScreen(this._from);
        let screenTo = viewerService.positionWorldToScreen(this._to);
        context.moveTo(screenFrom.x, screenFrom.y);
        context.lineTo(screenTo.x, screenTo.y);
        context.stroke();
    }

    get from(): Point {
        return this._from;
    }
    set from(value: Point) {
        this._from = value;
    }

    get to(): Point {
        return this._to;
    }
    set to(value: Point) {
        this._to = value;
    }

    get color(): string {
        return this._color;
    }
    set color(value: string) {
        this._color = value;
    }

    get width(): number {
        return this._width;
    }
    set width(value: number) {
        this._width = value;
    }

}