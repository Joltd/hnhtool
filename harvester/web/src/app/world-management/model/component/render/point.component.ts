import {Point} from "../../point";
import {Renderable} from "./renderable";
import {Box} from "../../box";

export class PointComponent extends Renderable {

    private _position: Point = new Point(0,0);
    private _style: string = 'circle';
    private _color: string = '#0000FF';
    private _size: number = 1000;

    isIntersect(box: Box): boolean {
        return box.isContains(this._position);
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
