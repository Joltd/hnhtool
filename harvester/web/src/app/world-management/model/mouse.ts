import {Point} from "./point";
import {Box} from "./box";

export class Mouse {
    private _lmb: boolean = false;
    private _mmb: boolean = false;
    private _rmb: boolean = false;
    private _dragging: boolean = false;
    private _origin: Point;
    private _cursor: Point;

    get lmb(): boolean {
        return this._lmb;
    }
    set lmb(value: boolean) {
        this._lmb = value;
    }

    get mmb(): boolean {
        return this._mmb;
    }
    set mmb(value: boolean) {
        this._mmb = value;
    }

    get rmb(): boolean {
        return this._rmb;
    }
    set rmb(value: boolean) {
        this._rmb = value;
    }

    get dragging(): boolean {
        return this._dragging;
    }
    set dragging(value: boolean) {
        this._dragging = value;
    }

    get origin(): Point {
        return this._origin;
    }
    set origin(value: Point) {
        this._origin = value;
    }

    get cursor(): Point {
        return this._cursor;
    }
    set cursor(value: Point) {
        this._cursor = value;
    }

    getSelectionArea(): Box {
        if (!this._origin) {
            return null;
        }
        return new Box(
            new Point(
                Math.min(this._origin.x, this._cursor.x),
                Math.min(this._origin.y, this._cursor.y)
            ),
            new Point(
                Math.max(this._origin.x, this._cursor.x),
                Math.max(this._origin.y, this._cursor.y)
            )
        )
    }
}