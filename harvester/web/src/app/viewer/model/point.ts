export class Point {
    readonly x: number;
    readonly y: number;

    constructor(value: number | Point, y?: number) {
        if (value instanceof Point) {
            this.x = value.x;
            this.y = value.y;
        } else {
            this.x = value;
            this.y = y !== undefined
                ? y
                : value;
        }
    }

    withX(value: number | Point) {
        if (value instanceof Point) {
            return new Point(value.x, this.y);
        } else {
            return new Point(value, this.y);
        }
    }

    withY(value: number | Point) {
        if (value instanceof Point) {
            return new Point(this.x, value.y);
        } else {
            return new Point(this.x, value);
        }
    }

    negate() {
        return new Point(-this.x, -this.y);
    }

    add(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x + value.x, this.y + value.y);
        } else {
            return new Point(this.x + value, this.y + (y !== undefined ? y : value));
        }
    }

    sub(value: number | Point, y?: number): Point {
        if (value instanceof Point) {
            return new Point(this.x - value.x, this.y - value.y);
        } else {
            return new Point(this.x - value, this.y - (y !== undefined ? y : value));
        }
    }

    mul(value: number) {
        return new Point(this.x * value, this.y * value);
    }

    div(value: number) {
        return new Point(this.x / value, this.y / value);
    }

    round(value: number) {
        return new Point(
            this.x >= 0 ? Math.floor(this.x / value) : Math.ceil(this.x / value),
            this.y >= 0 ? Math.floor(this.y / value) : Math.ceil(this.y / value)
        ).mul(value);
    }

    equals(value: Point) {
        return value?.x == this.x && value?.y == this.y;
    }

    toString(): string {
        return '(' + this.x + ';' + this.y + ')';
    }
}
