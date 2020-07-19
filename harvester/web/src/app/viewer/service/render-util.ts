import {TinyColor} from "@ctrl/tinycolor";
import {Point} from "../model/point";

export class RenderUtil {

    public static readonly GRID: string = '#C9C9C9';
    public static readonly SELECTION: string = '#00FF00';
    public static readonly DISABLED: string = '#9E9E9E';

    static lighten(color: string) {
        return new TinyColor(color).lighten(30).toHexString();
    }

    static renderPrimitive(
        graphic: CanvasRenderingContext2D,
        position: Point,
        type: PrimitiveType,
        size: number,
        color: string,
        selected: boolean,
        hovered: boolean,
        disabled: boolean
    ) {
        if (hovered) {
            graphic.fillStyle = RenderUtil.lighten(color);
        } else if (disabled) {
            graphic.fillStyle = RenderUtil.DISABLED;
        } else {
            graphic.fillStyle = color;
        }

        switch (type) {
            case "CIRCLE":
                graphic.beginPath();
                graphic.arc(position.x, position.y, size, 0, 2 * Math.PI);
                graphic.fill();
                break;
            case "RECT":
                let from = position.sub(size / 2);
                graphic.fillRect(from.x, from.y, size, size);
                break;
        }

        if (selected) {
            graphic.strokeStyle = RenderUtil.SELECTION;
            switch (type) {
                case "CIRCLE":
                    graphic.beginPath();
                    graphic.arc(position.x, position.y, size, 0, 2 * Math.PI);
                    graphic.stroke();
                    break;
                case "RECT":
                    let from = position.sub(size / 2);
                    graphic.strokeRect(from.x, from.y, size, size);
                    break;
            }
        }
    }

}

export type PrimitiveType = "CIRCLE" | "RECT";