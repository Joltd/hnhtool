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

    isOverlaps(box: Box): boolean {
        return (this.to.x >= box.from.x && box.to.x >= this.from.x)
            && (this.to.y >= box.from.y && box.to.y >= this.from.y);
    }
}