import {Position} from "./components";

export class Area {
    private _id: number;
    private _from: Position = new Position();
    private _to: Position = new Position();

    get id(): number {
        return this._id;
    }
    set id(value: number) {
        this._id = value;
    }

    get from(): Position {
        return this._from;
    }
    set from(value: Position) {
        this._from = value;
    }

    get to(): Position {
        return this._to;
    }
    set to(value: Position) {
        this._to = value;
    }

}