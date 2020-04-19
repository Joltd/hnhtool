import {Point} from "./point";

export class Viewport {
    private multiplier: number = 100;
    private position: Point = new Point(0,0);
    private size: Point = new Point(1000, 1000);
    private zoom: number = 1;
    private zoomLevels: number[] = [0.5,1,2,4,8];
    private _cursor: Point = new Point(-1, -1);

    // ##################################################
    // #                                                #
    // #  Cursor                                        #
    // #                                                #
    // ##################################################

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
        this.size = new Point(screenWidth, screenHeight)
            .mul(this.multiplier)
            .mul(this.zoomLevels[this.zoom]);
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
        return this.sizeScreenToWorld(screenX, screenY).add(this.position);
    }

    positionWorldToScreen(worldPosition: Point): Point {
        return this.sizeWorldToScreen(worldPosition.sub(this.position));
    }

    // ##################################################
    // #                                                #
    // #  Move                                          #
    // #                                                #
    // ##################################################

    move(screenDelta: Point) {
        this.position = this.sizeScreenToWorld(screenDelta.x, screenDelta.y)
            .negate()
            .add(this.position)
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
        this.size = this.size.div(2);
        this.position = position.sub(this.size.div(2));
    }

    public zoomOut(position: Point) {
        if (this.zoom == this.zoomLevels.length - 1) {
            return;
        }

        this.zoom++;
        this.size = this.size.mul(2);
        this.position = position.sub(this.size.div(2));
    }


}