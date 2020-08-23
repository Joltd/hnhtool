import {Position} from "./components";
import {Box} from "./box";
import {Point} from "./point";

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

    getBoundingBox(): Box {
        return new Box(
            new Point(
                Math.min(this._from.value.x, this._to.value.x),
                Math.min(this._from.value.y, this._to.value.y)
            ),
            new Point(
                Math.max(this._from.value.x, this._to.value.x),
                Math.max(this._from.value.y, this._to.value.y)
            )
        );
    }

}