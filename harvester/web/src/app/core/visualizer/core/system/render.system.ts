import {System} from "../system";
import {Entity} from "../entity";
import {PointRenderSystem} from "./point-render.system";
import {Viewport} from "../common/viewport";

export class RenderSystem implements System {

    private readonly context: CanvasRenderingContext2D;
    private readonly viewport: Viewport;
    private readonly pointRenderSystem: PointRenderSystem;

    constructor(context: CanvasRenderingContext2D, viewport: Viewport) {
        this.context = context;
        this.viewport = viewport;
        this.pointRenderSystem = new PointRenderSystem(this.viewport);
    }

    process(entities: Entity[]) {
        for (let entity of entities) {
            this.pointRenderSystem.process(entity, this.context);
        }
    }

}
