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

    isIntersectLine(from: Point, to: Point): boolean {
        return this.isContains(from)
            || this.isContains(to)
            || Box.isIntersectLine(from, to, this.from, this.from.withX(this.to))
            || Box.isIntersectLine(from, to, this.from.withX(this.to), this.to)
            || Box.isIntersectLine(from, to, this.to, this.to.withX(this.from))
            || Box.isIntersectLine(from, to, this.to.withX(this.from), this.from);
    }

    private static isIntersectLine(firstFrom: Point, firstTo: Point, secondFrom: Point, secondTo: Point): boolean {
        let uA = (
            (secondTo.x - secondFrom.x) * (firstFrom.y - secondFrom.y) - (secondTo.y - secondFrom.y) * (firstFrom.x - secondFrom.x)
        ) / (
            (secondTo.y - secondFrom.y) * (firstTo.x - firstFrom.x) - (secondTo.x - secondFrom.x) * (firstTo.y - firstFrom.y)
        );
        let uB = (
            (firstTo.x - firstFrom.x) * (firstFrom.y - secondFrom.y) - (firstTo.y - firstFrom.y) * (firstFrom.x - secondFrom.x)
        ) / (
            (secondTo.y - secondFrom.y) * (firstTo.x - firstFrom.x) - (secondTo.x - secondFrom.x) * (firstTo.y - firstFrom.y)
        );
        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }


}