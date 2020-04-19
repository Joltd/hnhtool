import {Component, ElementRef, NgZone, OnInit, ViewChild} from "@angular/core";
import {System} from "../core/system";
import {Entity} from "../core/entity";
import {PositionComponent} from "../core/component/position.component";
import {InputSystem} from "../core/system/input.system";
import {Viewport} from "../core/common/viewport";
import {Point} from "../core/common/point";
import {SelectableComponent} from "../core/component/selectable.component";
import {HoverableComponent} from "../core/component/hoverable.component";
import {PointComponent} from "../core/component/point.component";
import {RenderSystem} from "../core/system/render.system";

@Component({
    selector: 'visualizer',
    templateUrl: 'visualizer.component.html',
    styleUrls: ['visualizer.component.scss']
})
export class VisualizerComponent implements OnInit {

    @ViewChild('canvas', {static: true})
    canvas: ElementRef;
    context: CanvasRenderingContext2D;

    systems: System[] = [];
    entities: Entity[] = [];

    viewport: Viewport = new Viewport();
    inputSystem: InputSystem = new InputSystem(this.viewport, this.entities);

    counter: number = 0;

    constructor(private ngZone: NgZone) {}

    ngOnInit(): void {
        this.context = this.canvas.nativeElement.getContext('2d');

        let first = new Entity();
        first.addComponent(new PositionComponent(new Point(4500, 3500)));
        first.addComponent(new HoverableComponent());
        first.addComponent(new SelectableComponent());
        first.addComponent(new PointComponent())
        this.entities.push(first);

        let renderSystem = new RenderSystem(this.context, this.viewport);
        this.systems.push(renderSystem);


        this.ngZone.runOutsideAngular(() => {
            let loop = () => {
                this.main();
                requestAnimationFrame(loop);
            };
            requestAnimationFrame(loop);
        });
    }

    main() {
        let screenWidth = this.canvas.nativeElement.clientWidth;
        let screenHeight = this.canvas.nativeElement.clientHeight;
        this.canvas.nativeElement.width = screenWidth;
        this.canvas.nativeElement.height = screenHeight;

        this.viewport.resize(screenWidth, screenHeight);

        this.context.fillStyle = '#FF0000'
        this.context.fillRect(0, 0, screenWidth, screenHeight);

        for (let system of this.systems) {
            system.process(this.entities);
        }
    }

    onMouseMove(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.inputSystem.onMouseMove(event));
    }

    onMouseDown(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.inputSystem.onMouseDown(event))
    }

    onMouseUp(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.inputSystem.onMouseUp(event));
    }

    onMouseWheel(event: WheelEvent) {
        this.ngZone.runOutsideAngular(() => this.inputSystem.onMouseWheel(event));
    }

}
