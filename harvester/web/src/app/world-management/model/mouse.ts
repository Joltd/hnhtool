import {Point} from "./point";

export class Mouse {
    private _lmb: boolean = false;
    private _mmb: boolean = false;
    private _rmb: boolean = false;
    private _dragging: boolean = false;
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

    get cursor(): Point {
        return this._cursor;
    }
    set cursor(value: Point) {
        this._cursor = value;
    }
}