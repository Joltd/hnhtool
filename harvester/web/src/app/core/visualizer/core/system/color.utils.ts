import {TinyColor} from "@ctrl/tinycolor";

export class ColorUtils {

    static lighten(color: string): string {
        return new TinyColor(color).lighten(50).toHexString();
    }

}