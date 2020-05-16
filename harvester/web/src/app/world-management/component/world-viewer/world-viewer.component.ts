import {Component, ElementRef, NgZone, OnInit, ViewChild} from "@angular/core";
import {Point} from "../../model/point";
import {ViewerService} from "../../service/viewer.service";
import {Renderable} from "../../model/component/render/renderable";
import {PointComponent} from "../../model/component/render/point.component";
import {SelectableComponent} from "../../model/component/selectable.component";
import {HoverableComponent} from "../../model/component/hoverable.component";
import {RenderService} from "../../service/render.service";

@Component({
    selector: 'world-viewer',
    templateUrl: 'world-viewer.component.html',
    styleUrls: ['world-viewer.component.scss']
})
export class WorldViewerComponent implements OnInit {

    @ViewChild('canvas', {static: true})
    canvas: ElementRef;
    context: CanvasRenderingContext2D;

    constructor(
        private ngZone: NgZone,
        private viewerService: ViewerService,
        private renderService: RenderService
    ) {}

    ngOnInit(): void {
        this.context = this.canvas.nativeElement.getContext('2d');
        this.viewerService.canvas = this.context;

        let entity = this.viewerService.createEntity();
        let pointComponent = new PointComponent();
        pointComponent.position = new Point(10000, 10000);
        entity.addComponent(pointComponent, Renderable);
        entity.addComponent(new SelectableComponent());
        entity.addComponent(new HoverableComponent());

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

        this.viewerService.resizeViewport(screenWidth, screenHeight);

        this.renderGrid();
        this.renderSelectionArea();

        for (let entity of this.viewerService.queryEntities(Renderable)) {
            this.renderService.render(entity);
        }
    }

    renderGrid() {
        let position = this.viewerService.positionWorldToScreen(this.viewerService.viewportOffset().round(1024));
        let size = this.viewerService.sizeWorldToScreen(this.viewerService.viewportSize());
        let step = this.viewerService.sizeWorldToScreen(new Point(1024, 1024));

        this.context.strokeStyle = '#c9c9c9';
        this.context.beginPath();

        for (let x = position.x; x < position.x + size.x + step.x; x = x + step.x) {
            this.context.moveTo(x, 0);
            this.context.lineTo(x, size.y);
        }

        for (let y = position.y; y < position.y + size.y + step.y; y = y + step.y) {
            this.context.moveTo(0, y);
            this.context.lineTo(size.x, y);
        }

        this.context.stroke();
    }

    renderSelectionArea() {
        let selectionArea = this.viewerService.selectionArea();
        if (!selectionArea) {
            return;
        }

        let from = this.viewerService.positionWorldToScreen(selectionArea.from);
        let to = this.viewerService.positionWorldToScreen(selectionArea.to);
        this.context.strokeStyle = '#88a1ff'
        this.context.beginPath();
        this.context.moveTo(from.x, from.y);
        this.context.lineTo(to.x, from.y);
        this.context.lineTo(to.x, to.y);
        this.context.lineTo(from.x, to.y);
        this.context.lineTo(from.x, from.y);
        this.context.stroke();
    }

    onMouseMove(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.viewerService.onMouseMove(event));
    }

    onMouseDown(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.viewerService.onMouseDown(event))
    }

    onMouseUp(event: MouseEvent) {
        this.ngZone.runOutsideAngular(() => this.viewerService.onMouseUp(event));
    }

    onMouseWheel(event: WheelEvent) {
        this.ngZone.runOutsideAngular(() => this.viewerService.onMouseWheel(event));
    }

}