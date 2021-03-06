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
            case 'CIRCLE': {
                graphic.beginPath();
                graphic.arc(position.x, position.y, size, 0, 2 * Math.PI);
                graphic.fill();
                break;
            }
            case 'SQUARE': {
                let from = position.sub(size / 2);
                graphic.fillRect(from.x, from.y, size, size);
                break;
            }
            case 'STROKE_SQUARE': {
                graphic.strokeStyle = graphic.fillStyle;
                let from = position.sub(size / 2);
                graphic.strokeRect(from.x, from.y, size, size);
                break;
            }
        }

        if (selected) {
            graphic.strokeStyle = RenderUtil.SELECTION;
            switch (type) {
                case 'CIRCLE': {
                    graphic.beginPath();
                    graphic.arc(position.x, position.y, size, 0, 2 * Math.PI);
                    graphic.stroke();
                    break;
                }
                case 'SQUARE': {
                    let from = position.sub(size / 2);
                    graphic.strokeRect(from.x, from.y, size, size);
                    break;
                }
                case 'STROKE_SQUARE': {
                    let from = position.sub(size / 2);
                    graphic.strokeRect(from.x, from.y, size, size);
                    break;
                }
            }
        }
    }

    static renderLine(
        graphic: CanvasRenderingContext2D,
        from: Point,
        to: Point,
        size: number,
        color: string
    ) {
        graphic.strokeStyle = color;
        graphic.lineWidth = size;
        graphic.beginPath();
        graphic.moveTo(from.x, from.y);
        graphic.lineTo(to.x, to.y);
        graphic.stroke();
    }

    static renderRect(
        graphic: CanvasRenderingContext2D,
        from: Point,
        to: Point,
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

        let size = to.sub(from).abs();
        let x = Math.min(from.x, to.x);
        let y = Math.min(from.y, to.y);
        graphic.fillRect(x,y, size.x,size.y);

        if (selected) {
            graphic.strokeStyle = RenderUtil.SELECTION;
            graphic.beginPath();
            graphic.moveTo(x, y);
            graphic.lineTo(x + size.x, y);
            graphic.lineTo(x + size.x, y + size.y);
            graphic.lineTo(x, y + size.y);
            graphic.lineTo(x, y);
            graphic.stroke();
        }
    }
}

export type PrimitiveType = 'CIRCLE' | 'SQUARE' | 'STROKE_SQUARE';