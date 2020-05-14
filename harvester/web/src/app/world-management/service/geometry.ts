import {Point} from "../model/point";

export class Geometry {

    public static isPointInRect(point: Point, from: Point, to: Point) {
        return point.x >= from.x && point.x <= to.x && point.y >= from.y && point.y <= to.y;
    }

}