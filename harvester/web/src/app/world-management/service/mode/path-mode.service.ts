import {ViewerService} from "../viewer.service";
import {SelectableComponent} from "../../model/component/selectable.component";
import {Mode} from "./mode.service";
import {Command} from "../../model/command";
import {Injectable} from "@angular/core";
import {HoverableComponent} from "../../model/component/hoverable.component";
import {PointComponent} from "../../model/component/render/point.component";
import {Point} from "../../model/point";
import {LineComponent} from "../../model/component/render/line.component";
import {Renderable} from "../../model/component/render/renderable";

@Injectable()
export class PathModeService implements Mode {

    constructor(
        private viewerService: ViewerService
    ) {
        this.createLine(10*1024, 10*1024, 12*1024, 10*1024);
        this.createLine(12*1024, 10*1024, 12*1024, 12*1024);
        this.createLine(12*1024, 12*1024, 10*1024, 12*1024);
        this.createLine(10*1024, 12*1024, 10*1024, 10*1024);
        this.createPoint(10*1024, 10*1024);
        this.createPoint(12*1024, 10*1024);
        this.createPoint(12*1024, 12*1024);
        this.createPoint(10*1024, 12*1024);
    }

    commands(): Command[] {
        return [
            new Command('','Delete', '', 'Delete', () => this.remove())
        ];
    }

    remove() {
        this.viewerService.queryEntities(entity => {
            let selectable = entity.getComponent(SelectableComponent);
            return selectable && selectable.selected;
        }).forEach(entity => this.viewerService.removeEntity(entity.id));
    }

    createPoint(x: number, y: number) {
        let entity = this.viewerService.createEntity();
        entity.addComponent(new SelectableComponent());
        entity.addComponent(new HoverableComponent());
        let point = new PointComponent();
        point.color = '#000000';
        point.size = 450;
        point.position = new Point(x, y);
        entity.addComponent(point, Renderable);
    }

    createLine(fromX: number, fromY: number, toX: number, toY: number) {
        let entity = this.viewerService.createEntity();
        entity.addComponent(new SelectableComponent());
        entity.addComponent(new HoverableComponent());
        let line = new LineComponent();
        line.color = '#000000'
        line.width = 100;
        line.from = new Point(fromX, fromY);
        line.to = new Point(toX, toY);
        entity.addComponent(line, Renderable);
    }

}