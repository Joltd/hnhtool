import {Component, ElementRef, HostListener, ViewChild} from "@angular/core";
import {Space} from "../model/space";
import {KnownObject} from "../model/known-object";
import {Point} from "../model/point";
import {WorldViewerService} from "../service/world-viewer.service";

@Component({
    selector: 'some',
    templateUrl: 'world-viewer.component.html',
    styleUrls: ['world-viewer.component.scss']
})
export class WorldViewerComponent {

  private static readonly MULTIPLIER = 100.0;

  @ViewChild('world', {static: true})
  svg: ElementRef;

  spaces: Space[];
  space: Space;
  knownObjects: KnownObject[];
  offset: Point = this.worldToSvg(-1046016, -971264);
  size: Point;
  dragging: Point;

  constructor(public worldViewerService: WorldViewerService) {}

  ngOnInit(): void {
    this.onResize();

    this.worldViewerService.loadSpaces().subscribe(result => {
      this.spaces = result;
      this.space = this.spaces[0];
      this.updateKnownObjects();
    });
  }

  @HostListener('window: resize')
  onResize() {
    this.size = new Point(window.innerWidth, window.innerHeight);
  }

  selectSpace(space: Space) {
    this.space = space;
  }

  updateKnownObjects() {
    this.worldViewerService.loadKnownObjects(this.space.id, this.svgToWorld(this.offset), this.svgToWorld(this.offset.add(this.size)))
        .subscribe(result => this.knownObjects = result);
  }

  startDrag(event) {
    this.dragging = new Point(
        event.clientX,
        event.clientY
    );
  }

  drag(event) {
    if (!this.dragging) {
      return;
    }

    this.offset = this.dragging.sub(event.clientX, event.clientY).add(this.offset);
    this.dragging = new Point(event.clientX, event.clientY);
  }

  endDrag(event) {
    this.dragging = null;
    this.updateKnownObjects();
  }

  worldToSvg(value: number | Point, y?: number): Point {
    if (value instanceof Point) {
      return new Point(value.x / WorldViewerComponent.MULTIPLIER, value.y / WorldViewerComponent.MULTIPLIER);
    }
    return new Point(value / WorldViewerComponent.MULTIPLIER, y / WorldViewerComponent.MULTIPLIER);
  }

  svgToWorld(value: number | Point, y?: number): Point {
    if (value instanceof Point) {
      return new Point(value.x * WorldViewerComponent.MULTIPLIER, value.y * WorldViewerComponent.MULTIPLIER);
    }
    return new Point(value * WorldViewerComponent.MULTIPLIER, y * WorldViewerComponent.MULTIPLIER);
  }

  pointToString(point: Point) {
    return point.x + " " + point.y;
  }

}
