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
import {Entity} from "../../model/entity";

@Injectable()
export class PathModeService implements Mode {

    private links: Map<string, Entity[]> = new Map<string, Entity[]>();

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
        }).forEach(entity => {
            if (entity.getComponent(PointComponent)) {
                this.removeVertex(entity);
            } else if (entity.getComponent(LineComponent)) {
                this.removeEdge(entity);
            }
        });
    }

    private removeEdge(entity: Entity) {
        this.viewerService.removeEntity(entity.id);

        let line = entity.getComponent(LineComponent);
        this.removeLink(line.from, entity.id);
        this.removeLink(line.to, entity.id);
    }

    private removeVertex(entity: Entity) {
        this.viewerService.removeEntity(entity.id);

        let point = entity.getComponent(PointComponent);
        let linkedEntities = this.links.get(point.position.toString());
        this.links.delete(point.position.toString());
        for (let linkedEntity of linkedEntities) {
            if (linkedEntity.id == entity.id) {
                continue;
            }

            this.removeEdge(linkedEntity);
        }
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

        this.addLink(point.position, entity);
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

        this.addLink(line.from, entity);
        this.addLink(line.to, entity);
    }

    private addLink(point: Point, entity: Entity) {
        let links = this.links.get(point.toString());
        if (!links) {
            links = [];
            this.links.set(point.toString(), links);
        }

        links.push(entity);
    }

    private removeLink(point: Point, id: number) {
        let links = this.links.get(point.toString());
        if (!links) {
            return
        }

        let index = links.findIndex(entity => entity.id == id);
        if (index != -1) {
            links.splice(index, 1);
        }
    }
}