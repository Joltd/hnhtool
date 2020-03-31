import {Point} from "./point";

export class KnownObject {
    resource: string;
    position: Point;

    constructor(value: any) {
        this.resource = value.resource;
        this.position = new Point(value.position);
    }
}