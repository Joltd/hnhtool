import {Component, ElementRef, NgZone, OnInit, ViewChild} from "@angular/core";
import {Point} from "../../model/point";
import {ViewerService} from "../../service/viewer.service";

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
        private viewerService: ViewerService
    ) {}

    ngOnInit(): void {
        this.context = this.canvas.nativeElement.getContext('2d');

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

        // for (let entity of this.entityService.entities()) {
        //     let component = entity.getComponent(Renderable);
        //     if (component) {
        //         component.render(this.context, this.viewportService);
        //     }
        // }
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