import {Point} from "./point";
import {PrimitiveType} from "../service/render-util";

export class Disabled {}

export class Hoverable {
    private _value: boolean = false;

    get value(): boolean {
        return this._value;
    }

    set value(value: boolean) {
        this._value = value;
    }
}

export class Selectable {
    private _value: boolean = false;

    get value(): boolean {
        return this._value;
    }

    set value(value: boolean) {
        this._value = value;
    }
}

export class Movement {}

export class FollowCursor {}

export class Delta {
    private _value: Point = new Point(0,0);

    get value(): Point {
        return this._value;
    }

    set value(value: Point) {
        this._value = value;
    }
}

export class Position {
    private _value: Point;

    get value(): Point {
        return this._value;
    }

    set value(value: Point) {
        this._value = value;
    }
}

export class Primitive {
    private _type: PrimitiveType = "SQUARE";
    private _size: number = 500;
    private _color: string = '#0000FF';

    get type(): PrimitiveType {
        return this._type;
    }
    set type(value: PrimitiveType) {
        this._type = value;
    }

    get size(): number {
        return this._size;
    }
    set size(value: number) {
        this._size = value;
    }

    get color(): string {
        return this._color;
    }
    set color(value: string) {
        this._color = value;
    }

}

export class Tooltip {
    private _value: (Entity) => String;

    get value(): (Entity) => String {
        return this._value;
    }

    set value(value: (Entity) => String) {
        this._value = value;
    }
}