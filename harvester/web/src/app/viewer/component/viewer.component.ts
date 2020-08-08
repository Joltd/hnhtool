import {Component, ElementRef, NgZone, OnInit, ViewChild} from "@angular/core";
import {Point} from "../model/point";
import {Entity} from "../model/entity";
import {Box} from "../model/box";
import {Delta, Disabled, FollowCursor, Hoverable, Movement, Position, Primitive, Selectable} from "../model/components";
import {ViewerService} from "../service/viewer.service";
import {RenderUtil} from "../service/render-util";
import {Warehouse} from "../model/warehouse";
import {WarehouseService} from "../service/warehouse.service";
import {PathService} from "../service/path.service";
import {Path} from "../model/path";
import {KnownObjectService} from "../service/known-object.service";

@Component({
    selector: 'viewer',
    templateUrl: 'viewer.component.html',
    styleUrls: ['viewer.component.scss']
})
export class ViewerComponent implements OnInit {

    @ViewChild('canvas', {static: true})
    canvas: ElementRef;

    graphic: CanvasRenderingContext2D;

    constructor(
        private ngZone: NgZone,
        public viewerService: ViewerService,
        private knownObjectService: KnownObjectService,
        private warehouseService: WarehouseService,
        private pathService: PathService
    ) {}

    ngOnInit(): void {
        this.viewerService.load()
            .subscribe(() => {
                this.knownObjectService.load();
                this.warehouseService.load();
                this.pathService.load();
            })

        this.graphic = this.canvas.nativeElement.getContext('2d');

        this.ngZone.runOutsideAngular(() => {
            let loop = () => {
                this.render();
                requestAnimationFrame(loop);
            };
            requestAnimationFrame(loop);
        });
    }

    // ##################################################
    // #                                                #
    // #  Input handling                                #
    // #                                                #
    // ##################################################

    runOutside(event, handler) {
        this.ngZone.runOutsideAngular(() => handler.call(this, event));
    }

    onMouseMove(event: MouseEvent) {
        if (this.viewerService.mouse.lmb) {
            this.viewerService.mouse.worldCurrent = this.viewerService.positionScreenToWorld(event.offsetX, event.offsetY);
            this.handleMovement();
        } else if (this.viewerService.mouse.mmb) {
            let screen = new Point(event.offsetX, event.offsetY);
            let delta = screen.sub(this.viewerService.mouse.screenCurrent);
            this.viewerService.moveViewport(delta);
            this.viewerService.mouse.screenCurrent = screen;
        } else if (this.viewerService.mouse.rmb) {

        } else {
            this.viewerService.mouse.worldCurrent = this.viewerService.positionScreenToWorld(event.offsetX, event.offsetY);
            this.handleHovering();
        }
    }

    onMouseDown(event: MouseEvent) {
        if (event.button == 0) {
            this.viewerService.mouse.lmb = true;
            this.viewerService.mouse.worldCurrent = this.viewerService.positionScreenToWorld(event.offsetX, event.offsetY);
            this.viewerService.mouse.worldOrigin = this.viewerService.mouse.worldCurrent;
        } else if (event.button == 1) {
            this.viewerService.mouse.mmb = true;
            this.viewerService.mouse.screenCurrent = new Point(event.offsetX, event.offsetY);
        } else if (event.button == 2) {
            this.viewerService.mouse.rmb = true;
        }
    }

    onMouseUp(event: MouseEvent) {
        if (event.button == 0) {
            if (this.viewerService.isMovement) {
                this.viewerService.isMovement = false;
            } else {
                this.handleSelection();
                this.viewerService.onClick();
            }
            this.viewerService.mouse.lmb = false;
            this.viewerService.mouse.worldCurrent = null;
            this.viewerService.mouse.worldOrigin = null;
        } else if (event.button == 1) {
            this.viewerService.mouse.mmb = false;
            this.viewerService.mouse.screenCurrent = null;
        } else if (event.button == 2) {
            this.viewerService.onCancel();
            this.viewerService.mouse.rmb = false;
        }
    }

    onMouseWheel(event: WheelEvent) {
        this.viewerService.moveViewportTo(this.viewerService.mouse.worldCurrent);

        let viewport = this.viewerService.viewport;
        let prevZoom = ViewerService.ZOOM_LEVELS[viewport.zoom];

        if (event.deltaY < 0) {
            if (viewport.zoom == 0) {
                return;
            }
            viewport.zoom--;
        } else {
            if (viewport.zoom == ViewerService.ZOOM_LEVELS.length - 1) {
                return;
            }
            viewport.zoom++;
        }

        let newZoom = ViewerService.ZOOM_LEVELS[viewport.zoom];
        let prevSize = viewport.size;
        let newSize = prevSize.mul(newZoom).div(prevZoom);
        viewport.offset = viewport.offset.add(prevSize.sub(newSize).div(2));

    }

    onKeyUp(event: KeyboardEvent) {
        if (event.key == 'Escape') {
            this.viewerService.onCancel();
        }
    }

    // ##################################################
    // #                                                #
    // #  Hovering                                      #
    // #                                                #
    // ##################################################

    private handleHovering() {
        let area = this.viewerService.mouse.area;

        let hovered: Entity[] = [];

        for (let entity of this.viewerService.entities) {
            let disabled = entity.get(Disabled);

            let hoverable = entity.get(Hoverable);
            if (hoverable) {
                hoverable.value = this.isIntersect(entity, area) && !disabled;
                if (hoverable.value) {
                    hovered.push(entity);
                }
            }

            let followCursor = entity.get(FollowCursor);
            if (followCursor && !disabled) {
                ViewerComponent.setEntityPosition(entity, this.viewerService.mouse.worldCurrentRounded);
            }
        }

        if (hovered.length > 0) {
            this.viewerService.onHover(hovered);
        }
    }

    // ##################################################
    // #                                                #
    // #  Selection                                     #
    // #                                                #
    // ##################################################

    private handleSelection() {
        let area = this.viewerService.selectionArea;
        this.viewerService.hasSelection = false;
        for (let entity of this.viewerService.entities) {
            let selectable = entity.get(Selectable);
            let disabled = entity.get(Disabled);
            if (selectable) {
                selectable.value = this.isIntersect(entity, area) && !disabled;
                this.viewerService.hasSelection = this.viewerService.hasSelection || selectable.value;
            }
        }
    }

    // ##################################################
    // #                                                #
    // #  Movement                                      #
    // #                                                #
    // ##################################################

    private handleMovement() {
        let isMovement = false;
        for (let entity of this.viewerService.entities) {
            let selectable = entity.get(Selectable);
            let movement = entity.get(Movement);
            let disabled = entity.get(Disabled);
            if (selectable && selectable.value && movement && !disabled) {
                if (!this.viewerService.isMovement) {
                    ViewerComponent.setEntityDelta(entity, this.viewerService.mouse.worldCurrentRounded);
                    isMovement = true;
                } else {
                    ViewerComponent.setEntityPosition(entity, this.viewerService.mouse.worldCurrentRounded);
                }
            }
        }
        if (isMovement) {
            this.viewerService.isMovement = isMovement;
        }
    }

    private static setEntityDelta(entity: Entity, originPosition: Point) {
        let position = entity.get(Position);
        if (position) {
            entity.add(new Delta()).value = originPosition.sub(position.value);
        }
    }

    private static setEntityPosition(entity: Entity, newPosition: Point) {
        let position = entity.get(Position);
        if (position) {
            let delta = entity.get(Delta);
            position.value = delta
                ? newPosition.sub(delta.value)
                : newPosition;
        }
    }

    // ##################################################
    // #                                                #
    // #  Intersection                                  #
    // #                                                #
    // ##################################################

    private isIntersect(entity: Entity, box: Box): boolean {
        let primitive = entity.get(Primitive);
        let position = entity.get(Position);
        if (primitive && position) {
            return this.isIntersectPrimitive(primitive, position, box);
        }
        if (entity.get(Warehouse)) {
            return this.isIntersectWarehouse(entity, box);
        }
        return false;
    }

    private isIntersectPrimitive(primitive: Primitive, position: Position, box: Box): boolean {
        let from = position.value.sub(primitive.size / 2);
        let boundingBox = new Box(from, from.add(primitive.size));
        return boundingBox.isOverlaps(box);
    }

    private isIntersectWarehouse(entity: Entity, box: Box): boolean {
        let warehouse = entity.get(Warehouse);
        for (let cell of warehouse.cells) {
            let from = cell.position.sub(500 / 2);
            let boundingBox = new Box(from, from.add(500));
            if (boundingBox.isOverlaps(box)) {
                return true;
            }
        }
        return false;
    }

    // ##################################################
    // #                                                #
    // #  Rendering                                     #
    // #                                                #
    // ##################################################

    private render() {
        let screenWidth = this.canvas.nativeElement.clientWidth;
        let screenHeight = this.canvas.nativeElement.clientHeight;
        this.canvas.nativeElement.width = screenWidth;
        this.canvas.nativeElement.height = screenHeight;

        this.viewerService.resizeViewport(screenWidth, screenHeight);

        this.renderGrid();
        this.renderEntities();
        this.renderSelectionArea();
    }

    private renderGrid() {
        let position = this.viewerService.positionWorldToScreen(this.viewerService.viewport.offset.round(ViewerService.GRID_STEP).add(ViewerService.GRID_STEP / 2));
        let size = this.viewerService.sizeWorldToScreen(this.viewerService.viewport.size);
        let step = this.viewerService.sizeWorldToScreen(new Point(ViewerService.GRID_STEP));

        this.graphic.strokeStyle = RenderUtil.GRID;
        this.graphic.beginPath();

        for (let x = position.x; x < position.x + size.x + step.x; x = x + step.x) {
            this.graphic.moveTo(x, 0);
            this.graphic.lineTo(x, size.y);
        }

        for (let y = position.y; y < position.y + size.y + step.y; y = y + step.y) {
            this.graphic.moveTo(0, y);
            this.graphic.lineTo(size.x, y);
        }

        this.graphic.stroke();
    }

    private renderEntities() {
        for (let entity of this.viewerService.entities) {
            if (entity.has(Primitive)) {
                this.renderPrimitive(entity);
            } else if (entity.has(Warehouse)) {
                this.renderWarehouse(entity);
            } else if (entity.has(Path)) {
                this.renderPath(entity);
            }

            if (entity.has(Disabled)) {
                let selectable = entity.get(Selectable);
                if (selectable) {
                    selectable.value = false;
                }
                let hoverable = entity.get(Hoverable);
                if (hoverable) {
                    hoverable.value = false;
                }
            }
        }
    }

    private renderPrimitive(entity: Entity) {
        let primitive = entity.get(Primitive);
        RenderUtil.renderPrimitive(
            this.graphic,
            this.viewerService.positionWorldToScreen(entity.get(Position).value),
            primitive.type,
            this.viewerService.sizeWorldToScreen(primitive.size).x,
            primitive.color,
            entity.get(Selectable)?.value,
            entity.get(Hoverable)?.value,
            entity.has(Disabled)
        );
    }

    private renderWarehouse(entity: Entity) {
        let warehouse = entity.get(Warehouse);
        for (let cell of warehouse.cells) {
            RenderUtil.renderPrimitive(
                this.graphic,
                this.viewerService.positionWorldToScreen(cell.position),
                "RECT",
                this.viewerService.sizeWorldToScreen(500).x,
                '#00004F',
                entity.get(Selectable)?.value,
                entity.get(Hoverable)?.value,
                entity.has(Disabled)
            );
        }
    }

    private renderPath(entity: Entity) {
        let path = entity.get(Path);
        for (let edge of path.edges) {
            RenderUtil.renderLine(
                this.graphic,
                this.viewerService.positionWorldToScreen(edge.from.value),
                this.viewerService.positionWorldToScreen(edge.to.value),
                this.viewerService.sizeWorldToScreen(100).x,
                '#FFFF00'
            )
        }
    }

    private renderSelectionArea() {
        if (!this.viewerService.mouse.worldOrigin) {
            return;
        }

        let selectionArea = this.viewerService.selectionArea;
        if (!selectionArea || this.viewerService.hasSelection) {
            return;
        }

        let from = this.viewerService.positionWorldToScreen(selectionArea.from);
        let to = this.viewerService.positionWorldToScreen(selectionArea.to);
        this.graphic.strokeStyle = '#88a1ff'
        this.graphic.beginPath();
        this.graphic.moveTo(from.x, from.y);
        this.graphic.lineTo(to.x, from.y);
        this.graphic.lineTo(to.x, to.y);
        this.graphic.lineTo(from.x, to.y);
        this.graphic.lineTo(from.x, from.y);
        this.graphic.stroke();
    }

}