import {Point} from "./point";

export class Viewport {

    public static readonly MULTIPLIER: number = 100;
    public static readonly ZOOM_LEVELS: number[] = [0.25, 0.5, 1, 2];

    private _offset: Point = new Point(0, 0);
    private _size: Point = new Point(1000, 1000);
    private _zoom: number = 2;

    get offset(): Point {
        return this._offset;
    }
    set offset(value: Point) {
        this._offset = value;
    }

    get size(): Point {
        return this._size;
    }
    set size(value: Point) {
        this._size = value;
    }

    get zoom(): number {
        return this._zoom;
    }
    set zoom(value: number) {
        this._zoom = value;
    }

}