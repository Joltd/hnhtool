import {ViewerService} from "../../../service/viewer.service";
import {Box} from "../../box";

export class Renderable {

    isIntersect(box: Box): boolean {
        throw 'Unsupported';
    }

    render(context: CanvasRenderingContext2D, viewerService: ViewerService) {
        throw 'Unsupported';
    }

}