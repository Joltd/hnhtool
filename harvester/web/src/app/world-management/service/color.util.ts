import {TinyColor} from "@ctrl/tinycolor";

export class ColorUtil {

    static lighten(color: string) {
        return new TinyColor(color).lighten(30).toHexString();
    }

}