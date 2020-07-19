import {Point} from "./point";

export class Warehouse {

    private _id: number;
    private _cells: Cell[] = [];

    get id(): number {
        return this._id;
    }
    set id(value: number) {
        this._id = value;
    }

    get cells(): Cell[] {
        return this._cells;
    }
    set cells(value: Cell[]) {
        this._cells = value;
    }

}

export class Cell {

    private _id: number;
    private _position: Point;

    get id(): number {
        return this._id;
    }
    set id(value: number) {
        this._id = value;
    }

    get position(): Point {
        return this._position;
    }
    set position(value: Point) {
        this._position = value;
    }

}