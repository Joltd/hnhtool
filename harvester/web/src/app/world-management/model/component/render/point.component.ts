import {Point} from "../../point";
import {Renderable} from "./renderable";
import {ViewerService} from "../../../service/viewer.service";
import {Box} from "../../box";

export class PointComponent extends Renderable {

    private _position: Point = new Point(0,0);
    private _style: string = 'circle';
    private _color: string = '#00FF00';
    private _size: number = 1000;

    isIntersect(box: Box): boolean {
        return box.isContains(this._position);
    }

    render(context: CanvasRenderingContext2D, viewerService: ViewerService) {
        context.fillStyle = this._color;
        let position = viewerService.positionWorldToScreen(this._position);
        let size = viewerService.sizeWorldToScreen(new Point(this._size, 0)).x;
        context.beginPath();
        context.arc(position.x, position.y, size, 0, 2 * Math.PI);
        context.fill();
    }

    get position(): Point {
        return this._position;
    }
    set position(value: Point) {
        this._position = value;
    }

    get style(): string {
        return this._style;
    }
    set style(value: string) {
        this._style = value;
    }

    get color(): string {
        return this._color;
    }
    set color(value: string) {
        this._color = value;
    }

    get size(): number {
        return this._size;
    }
    set size(value: number) {
        this._size = value;
    }

}
