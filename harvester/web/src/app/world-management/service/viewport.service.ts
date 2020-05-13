import {Injectable} from "@angular/core";
import {Point} from "../model/point";

@Injectable()
export class ViewportService {

    private multiplier: number = 100;
    private _position: Point = new Point(0,0);
    private _size: Point = new Point(1000, 1000);
    private zoom: number = 1;
    private zoomLevels: number[] = [0.5,1,2,4,8];
    private _cursor: Point = new Point(-1, -1);

    // ##################################################
    // #                                                #
    // #  Accessors                                     #
    // #                                                #
    // ##################################################


    get position(): Point {
        return this._position;
    }

    set position(value: Point) {
        this._position = value;
    }

    get size(): Point {
        return this._size;
    }

    set size(value: Point) {
        this._size = value;
    }

    get cursor(): Point {
        return this._cursor;
    }

    set cursor(value: Point) {
        this._cursor = value;
    }

    // ##################################################
    // #                                                #
    // #  Resize                                        #
    // #                                                #
    // ##################################################

    resize(screenWidth: number, screenHeight: number) {
        this._size = this.sizeScreenToWorld(screenWidth, screenHeight);
    }

    // ##################################################
    // #                                                #
    // #  Convert                                       #
    // #                                                #
    // ##################################################

    sizeScreenToWorld(screenWidth: number, screenHeight: number): Point {
        return new Point(screenWidth, screenHeight)
            .mul(this.multiplier)
            .mul(this.zoomLevels[this.zoom]);
    }

    sizeWorldToScreen(worldSize: Point): Point {
        return worldSize
            .div(this.multiplier)
            .div(this.zoomLevels[this.zoom]);
    }

    positionScreenToWorld(screenX: number, screenY: number): Point {
        return this.sizeScreenToWorld(screenX, screenY).add(this._position);
    }

    positionWorldToScreen(worldPosition: Point): Point {
        return this.sizeWorldToScreen(worldPosition.sub(this._position));
    }

    // ##################################################
    // #                                                #
    // #  Move                                          #
    // #                                                #
    // ##################################################

    move(screenDelta: Point) {
        this._position = this.sizeScreenToWorld(screenDelta.x, screenDelta.y)
            .negate()
            .add(this._position)
    }

    // ##################################################
    // #                                                #
    // #  Zoom                                          #
    // #                                                #
    // ##################################################

    public zoomIn(position: Point) {
        if (this.zoom == 0) {
            return;
        }

        this.zoom--;
        this._size = this._size.div(2);
        this._position = position.sub(this._size.div(2));
    }

    public zoomOut(position: Point) {
        if (this.zoom == this.zoomLevels.length - 1) {
            return;
        }

        this.zoom++;
        this._size = this._size.mul(2);
        this._position = position.sub(this._size.div(2));
    }
}