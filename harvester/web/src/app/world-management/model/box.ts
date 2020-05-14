import {Point} from "./point";

export class Box {
    readonly from: Point;
    readonly to: Point;

    constructor(from: Point, to: Point) {
        this.from = from;
        this.to = to;
    }

    isContains(point: Point): boolean {
        return point.x >= this.from.x
            && point.x <= this.to.x
            && point.y >= this.from.y
            && point.y <= this.to.y;
    }
}