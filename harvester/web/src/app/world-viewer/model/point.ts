export class Point {
    x: number;
    y: number;

    constructor(value: number | any, y?: number) {
        if (typeof value === 'number') {
            this.x = value;
            this.y = y;
        } else {
            this.x = value.x;
            this.y = value.y;
        }
    }

    add(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x + value.x, this.y + value.y)
        }

        return new Point(this.x + value, this.y + y);
    }

    sub(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x - value.x, this.y - value.y)
        }

        return new Point(this.x - value, this.y - y);
    }

    mul(value: number): Point {
        return new Point(this.x * value, this.y * value);
    }

    div(value: number): Point {
        return new Point(this.x / value, this.y / value);
    }

    toString(): string {
        return "(" + this.x + "; " + this.y + ")";
    }
}