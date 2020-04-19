import {Component} from "../component";
import {Point} from "../common/point";

export class PositionComponent implements Component {

    private _position: Point;

    constructor(position: Point) {
        this._position = position;
    }

    get position(): Point {
        return this._position;
    }

    set position(value: Point) {
        this._position = value;
    }

}
