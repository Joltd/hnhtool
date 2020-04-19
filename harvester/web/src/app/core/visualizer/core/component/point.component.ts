import {Component} from "../component";

export class PointComponent implements Component {

    private _style: string = 'circle';
    private _color: string = '#00FF00';
    private _size: number = 1000;

    get style(): string {
        return this._style;
    }
    set style(value: string) {
        this._style = value;
    }

    get color(): string {
        return this._color;
    }
    set color(value: string) {
        this._color = value;
    }

    get size(): number {
        return this._size;
    }
    set size(value: number) {
        this._size = value;
    }

}
