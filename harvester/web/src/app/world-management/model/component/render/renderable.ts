import {Point} from "../../point";
import {ViewportService} from "../../../service/viewport.service";

export class Renderable {

    isIntersect(point: Point): boolean {
        throw 'Unsupported';
    }

    move(delta: Point) {
        throw 'Unsupported';
    }

    render(context: CanvasRenderingContext2D, viewportService: ViewportService) {
        throw 'Unsupported';
    }

}