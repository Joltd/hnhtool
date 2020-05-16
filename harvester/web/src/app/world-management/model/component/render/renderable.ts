import {Box} from "../../box";

export class Renderable {

    isIntersect(box: Box): boolean {
        throw 'Unsupported';
    }

}