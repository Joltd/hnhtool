export class Point {
    readonly x: number;
    readonly y: number;

    constructor(value: number | Point, y?: number) {
        if (value instanceof Point) {
            this.x = value.x;
            this.y = value.y;
        } else {
            this.x = value;
            this.y = y;
        }
    }

    negate() {
        return new Point(-this.x, -this.y);
    }

    add(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x + value.x, this.y + value.y);
        } else {
            return new Point(this.x + value, this.y + y);
        }
    }

    sub(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x - value.x, this.y - value.y);
        } else {
            return new Point(this.x - value, this.y - y);
        }
    }

    mul(value: number) {
        return new Point(this.x * value, this.y * value);
    }

    div(value: number) {
        return new Point(this.x / value, this.y / value);
    }
}
