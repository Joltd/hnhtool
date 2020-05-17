import {Point} from "../../point";
import {Renderable} from "./renderable";
import {Box} from "../../box";

export class LineComponent extends Renderable {

    private _from: Point = new Point(0,0);
    private _to: Point = new Point(0,0);
    private _color: string = '#0000FF';
    private _width: number = 100;

    isIntersect(box: Box): boolean {
        return this.isIntersectLine(box.from.x, box.from.y, box.to.x, box.from.y)
            || this.isIntersectLine(box.to.x, box.from.y, box.to.x, box.to.y)
            || this.isIntersectLine(box.to.x, box.to.y, box.from.x, box.to.y)
            || this.isIntersectLine(box.from.x, box.to.y, box.from.x, box.from.y)
            || box.isContains(this.from)
            || box.isContains(this.to);
    }

    isIntersectLine(fromX: number, fromY: number, toX: number, toY: number): boolean {
        let uA = ((toX-fromX)*(this.from.y-fromY) - (toY-fromY)*(this.from.x-fromX)) / ((toY-fromY)*(this.to.x-this.from.x) - (toX-fromX)*(this.to.y-this.from.y));
        let uB = ((this.to.x-this.from.x)*(this.from.y-fromY) - (this.to.y-this.from.y)*(this.from.x-fromX)) / ((toY-fromY)*(this.to.x-this.from.x) - (toX-fromX)*(this.to.y-this.from.y));
        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }

    get from(): Point {
        return this._from;
    }
    set from(value: Point) {
        this._from = value;
    }

    get to(): Point {
        return this._to;
    }
    set to(value: Point) {
        this._to = value;
    }

    get color(): string {
        return this._color;
    }
    set color(value: string) {
        this._color = value;
    }

    get width(): number {
        return this._width;
    }
    set width(value: number) {
        this._width = value;
    }

}