import {Point} from "../../point";
import {Renderable} from "./renderable";
import {ViewportService} from "../../../service/viewport.service";

export class LineComponent extends Renderable {

    private _from: Point = new Point(0,0);
    private _to: Point = new Point(0,0);
    private _color: string = '#00FF00';
    private _width: number = 100;

    isIntersect(point: Point): boolean {
        return false;
    }

    move(delta: Point) {

    }

    render(context: CanvasRenderingContext2D, viewportService: ViewportService) {

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